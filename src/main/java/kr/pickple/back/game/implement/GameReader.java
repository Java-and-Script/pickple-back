package kr.pickple.back.game.implement;

import static kr.pickple.back.address.exception.AddressExceptionCode.*;
import static kr.pickple.back.chat.exception.ChatExceptionCode.*;
import static kr.pickple.back.game.exception.GameExceptionCode.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.address.domain.MainAddress;
import kr.pickple.back.address.exception.AddressException;
import kr.pickple.back.address.implement.AddressReader;
import kr.pickple.back.address.repository.AddressDepth1Repository;
import kr.pickple.back.address.repository.AddressDepth2Repository;
import kr.pickple.back.address.repository.entity.AddressDepth1Entity;
import kr.pickple.back.address.repository.entity.AddressDepth2Entity;
import kr.pickple.back.chat.exception.ChatException;
import kr.pickple.back.game.domain.Game;
import kr.pickple.back.game.domain.GameStatus;
import kr.pickple.back.game.exception.GameException;
import kr.pickple.back.game.repository.GamePositionRepository;
import kr.pickple.back.game.repository.GameRepository;
import kr.pickple.back.game.repository.entity.GameEntity;
import kr.pickple.back.game.repository.entity.GamePosition;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.implement.MemberReader;
import kr.pickple.back.position.domain.Position;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GameReader {

    private final AddressReader addressReader;
    private final AddressDepth1Repository addressDepth1Repository;
    private final AddressDepth2Repository addressDepth2Repository;
    private final MemberReader memberReader;
    private final GameRepository gameRepository;
    private final GamePositionRepository gamePositionRepository;

    public Game read(final Long gameId) {
        final GameEntity gameEntity = gameRepository.findById(gameId)
                .orElseThrow(() -> new GameException(GAME_NOT_FOUND, gameId));

        gameEntity.increaseViewCount();

        return mapGameEntityToDomain(gameEntity);
    }

    public Game readByChatRoomId(final Long chatRoomId) {
        final GameEntity gameEntity = gameRepository.findByChatRoomId(chatRoomId)
                .orElseThrow(() -> new ChatException(CHAT_GAME_NOT_FOUND, chatRoomId));

        return mapGameEntityToDomain(gameEntity);
    }

    public List<Game> readAllByHostId(final Long hostId) {
        return gameRepository.findAllByHostId(hostId)
                .stream()
                .map(this::mapGameEntityToDomain)
                .toList();
    }

    private Game mapGameEntityToDomain(final GameEntity gameEntity) {
        final Member host = memberReader.readByMemberId(gameEntity.getHostId());

        final List<Position> positions = readPositionsByGameId(gameEntity.getId());

        final MainAddress mainAddress = addressReader.readMainAddressByIds(
                gameEntity.getAddressDepth1Id(),
                gameEntity.getAddressDepth2Id()
        );

        return GameMapper.mapGameEntityToDomain(gameEntity, mainAddress, host, positions);
    }

    public List<Game> readAllByAddress(final String address, final Pageable pageable) {
        final PageRequest pageRequest = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(
                        Sort.Order.asc("playDate"),
                        Sort.Order.asc("playStartTime"),
                        Sort.Order.asc("id")
                )
        );

        final MainAddress mainAddress = addressReader.readMainAddressFromFullAddress(address);

        final Page<GameEntity> gameEntities = gameRepository.findByAddressDepth1IdAndAddressDepth2IdAndStatusNot(
                mainAddress.getAddressDepth1Id(),
                mainAddress.getAddressDepth2Id(),
                GameStatus.ENDED,
                pageRequest
        );

        return gameEntities.stream()
                .map(gameEntity -> GameMapper.mapGameEntityToDomain(
                        gameEntity,
                        mainAddress,
                        memberReader.readByMemberId(gameEntity.getHostId()),
                        readPositionsByGameId(gameEntity.getId())))
                .toList();
    }

    public List<Game> readAllWithInAddress(final MainAddress mainAddress) {
        final String addressDepth1Name = mainAddress.getAddressDepth1Name();
        final String addressDepth2Name = mainAddress.getAddressDepth2Name();

        final AddressDepth1Entity addressDepth1Entity = addressDepth1Repository.findByName(addressDepth1Name)
                .orElseThrow(() -> new AddressException(ADDRESS_NOT_FOUND, addressDepth1Name));
        final AddressDepth2Entity addressDepth2Entity = addressDepth2Repository.findByNameAndAddressDepth1Id(
                        addressDepth2Name, addressDepth1Entity.getId())
                .orElseThrow(() -> new AddressException(ADDRESS_NOT_FOUND, addressDepth1Name, addressDepth2Name));

        final List<GameEntity> gameEntities = gameRepository.findGamesWithInAddress(
                addressDepth1Entity,
                addressDepth2Entity
        );

        return gameEntities.stream()
                .map(gameEntity -> GameMapper.mapGameEntityToDomain(
                        gameEntity,
                        mainAddress,
                        memberReader.readByMemberId(gameEntity.getHostId()),
                        readPositionsByGameId(gameEntity.getId())))
                .toList();
    }

    public List<Game> readAllWithInDistance(
            final Double latitude,
            final Double longitude,
            final Double distance
    ) {
        final List<GameEntity> gameEntities = gameRepository.findGamesWithInDistance(latitude, longitude, distance);

        return gameEntities.stream()
                .map(gameEntity -> GameMapper.mapGameEntityToDomain(
                        gameEntity,
                        addressReader.readMainAddressByIds(
                                gameEntity.getAddressDepth1Id(),
                                gameEntity.getAddressDepth2Id()
                        ),
                        memberReader.readByMemberId(gameEntity.getHostId()),
                        readPositionsByGameId(gameEntity.getId())))
                .toList();
    }

    private List<Position> readPositionsByGameId(final Long gameId) {
        return gamePositionRepository.findAllByGameId(gameId)
                .stream()
                .map(GamePosition::getPosition)
                .toList();
    }
}
