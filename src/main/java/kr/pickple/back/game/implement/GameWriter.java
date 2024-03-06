package kr.pickple.back.game.implement;

import static kr.pickple.back.game.exception.GameExceptionCode.*;

import java.util.List;

import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.address.dto.response.MainAddress;
import kr.pickple.back.address.implement.AddressReader;
import kr.pickple.back.address.service.kakao.KakaoAddressSearchClient;
import kr.pickple.back.game.domain.GameDomain;
import kr.pickple.back.game.domain.GameStatus;
import kr.pickple.back.game.domain.NewGame;
import kr.pickple.back.game.exception.GameException;
import kr.pickple.back.game.repository.GameMemberRepository;
import kr.pickple.back.game.repository.GamePositionRepository;
import kr.pickple.back.game.repository.GameRepository;
import kr.pickple.back.game.repository.entity.GameEntity;
import kr.pickple.back.game.repository.entity.GamePosition;
import kr.pickple.back.position.domain.Position;
import lombok.RequiredArgsConstructor;

@Component
@Transactional
@RequiredArgsConstructor
public class GameWriter {

    private final AddressReader addressReader;
    private final GameRepository gameRepository;
    private final GameMemberRepository gameMemberRepository;
    private final KakaoAddressSearchClient kakaoAddressSearchClient;
    private final GamePositionRepository gamePositionRepository;

    public GameDomain create(final NewGame newGame) {
        final Point point = kakaoAddressSearchClient.fetchAddress(newGame.getMainAddress());

        final MainAddress mainAddress = addressReader.readMainAddressByNames(
                newGame.getAddressDepth1Name(),
                newGame.getAddressDepth2Name()
        );

        final GameEntity gameEntity = GameMapper.mapNewGameDomainToEntity(newGame, point, mainAddress);
        final GameEntity savedGameEntity = gameRepository.save(gameEntity);

        setPositionsToGame(newGame.getPositions(), savedGameEntity.getId());

        return GameMapper.mapToGameDomain(
                savedGameEntity,
                mainAddress,
                newGame.getHost(),
                newGame.getChatRoom(),
                newGame.getPositions()
        );
    }

    private void setPositionsToGame(final List<Position> positions, final Long gameId) {
        validateIsDuplicatedPositions(positions);

        final List<GamePosition> gamePositions = GameMapper.mapToGamePositionEntities(positions, gameId);

        gamePositionRepository.saveAll(gamePositions);
    }

    private void validateIsDuplicatedPositions(final List<Position> positions) {
        final Long distinctPositionsSize = positions.stream()
                .distinct()
                .count();

        if (distinctPositionsSize != positions.size()) {
            throw new GameException(GAME_POSITIONS_IS_DUPLICATED, positions);
        }
    }

    public void updateMemberRegistrationStatus(final GameStatus status, final Long gameId) {
        gameRepository.updateRegistrationStatus(status, gameId);
    }
}
