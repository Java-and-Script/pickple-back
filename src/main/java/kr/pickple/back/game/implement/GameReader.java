package kr.pickple.back.game.implement;

import static kr.pickple.back.game.exception.GameExceptionCode.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.address.dto.response.MainAddress;
import kr.pickple.back.address.implement.AddressReader;
import kr.pickple.back.chat.domain.ChatRoom;
import kr.pickple.back.chat.repository.ChatRoomRepository;
import kr.pickple.back.common.domain.RegistrationStatus;
import kr.pickple.back.game.domain.GameDomain;
import kr.pickple.back.game.domain.GameStatus;
import kr.pickple.back.game.exception.GameException;
import kr.pickple.back.game.repository.GameMemberRepository;
import kr.pickple.back.game.repository.GamePositionRepository;
import kr.pickple.back.game.repository.GameRepository;
import kr.pickple.back.game.repository.entity.GameEntity;
import kr.pickple.back.game.repository.entity.GamePosition;
import kr.pickple.back.member.domain.MemberDomain;
import kr.pickple.back.member.implement.MemberReader;
import kr.pickple.back.position.domain.Position;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GameReader {

    private final MemberReader memberReader;
    private final AddressReader addressReader;
    private final GameRepository gameRepository;
    private final GameMemberRepository gameMemberRepository;
    private final GamePositionRepository gamePositionRepository;
    private final ChatRoomRepository chatRoomRepository;

    public GameDomain read(final Long gameId) {
        final GameEntity gameEntity = readGameEntity(gameId);
        gameEntity.increaseViewCount();

        final MemberDomain host = memberReader.readByMemberId(gameEntity.getHostId());

        final List<Position> positions = readPositionsByGameId(gameId);

        final MainAddress mainAddress = addressReader.readMainAddressById(
                gameEntity.getAddressDepth1Id(),
                gameEntity.getAddressDepth2Id()
        );

        final ChatRoom chatRoom = chatRoomRepository.getChatRoomById(gameEntity.getChatRoomId());
        return GameMapper.mapGameEntityToDomain(gameEntity, mainAddress, host, chatRoom, positions);
    }

    private GameEntity readGameEntity(final Long gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new GameException(GAME_NOT_FOUND, gameId));
    }

    private List<Position> readPositionsByGameId(final Long gameId) {
        return gamePositionRepository.findAllByGameId(gameId)
                .stream()
                .map(GamePosition::getPosition)
                .toList();
    }

    public List<MemberDomain> readAllMembersByGameIdAndStatus(final Long gameId, final RegistrationStatus status) {
        return gameMemberRepository.findAllByGameIdAndStatus(gameId, status)
                .stream()
                .map(gameMemberEntity -> memberReader.readByMemberId(gameMemberEntity.getMemberId()))
                .toList();
    }

    public List<GameDomain> findGamesByAddress(final String address, final Pageable pageable) {

        final PageRequest pageRequest = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(
                        Sort.Order.asc("playDate"),
                        Sort.Order.asc("playStartTime"),
                        Sort.Order.asc("id")
                )
        );

        final MainAddress mainAddress = addressReader.readMainAddressByAddressStrings(address);

        final Page<GameEntity> gameEntities = gameRepository.findByAddressDepth1IdAndAddressDepth2IdAndStatusNot(
                mainAddress.getAddressDepth1().getId(),
                mainAddress.getAddressDepth2().getId(),
                GameStatus.ENDED,
                pageRequest
        );

        return gameEntities.stream()
                .map(gameEntity -> GameMapper.mapGameEntityToDomain(
                        gameEntity,
                        mainAddress,
                        memberReader.readByMemberId(gameEntity.getHostId()),
                        chatRoomRepository.getChatRoomById(gameEntity.getChatRoomId()),
                        readPositionsByGameId(gameEntity.getId())))
                .toList();
    }

    public List<GameDomain> findGamesWithInAddress(MainAddress mainAddress) {
        final List<GameEntity> gameEntities = gameRepository.findGamesWithInAddress(
                mainAddress.getAddressDepth1(),
                mainAddress.getAddressDepth2()
        );

        return gameEntities.stream()
                .map(gameEntity -> GameMapper.mapGameEntityToDomain(
                        gameEntity,
                        mainAddress,
                        memberReader.readByMemberId(gameEntity.getHostId()),
                        chatRoomRepository.getChatRoomById(gameEntity.getChatRoomId()),
                        readPositionsByGameId(gameEntity.getId())))
                .toList();
    }

    public List<GameDomain> findGamesWithInDistance(Double latitude, Double longitude, Double distance) {
        final List<GameEntity> gameEntities = gameRepository.findGamesWithInDistance(latitude, longitude, distance);

        return gameEntities.stream()
                .map(gameEntity -> GameMapper.mapGameEntityToDomain(
                        gameEntity,
                        addressReader.readMainAddressById(
                                gameEntity.getAddressDepth1Id(),
                                gameEntity.getAddressDepth2Id()
                        ),
                        memberReader.readByMemberId(gameEntity.getHostId()),
                        chatRoomRepository.getChatRoomById(gameEntity.getChatRoomId()),
                        readPositionsByGameId(gameEntity.getId())))
                .toList();
    }
}
