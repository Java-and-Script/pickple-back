package kr.pickple.back.game.implement;

import java.util.List;

import org.locationtech.jts.geom.Point;

import kr.pickple.back.address.dto.response.MainAddress;
import kr.pickple.back.game.domain.Game;
import kr.pickple.back.game.domain.GameMember;
import kr.pickple.back.game.domain.NewGame;
import kr.pickple.back.game.repository.entity.GameEntity;
import kr.pickple.back.game.repository.entity.GameMemberEntity;
import kr.pickple.back.game.repository.entity.GamePosition;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.position.domain.Position;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GameMapper {

    public static Game mapGameEntityToDomain(
            final GameEntity gameEntity,
            final MainAddress mainAddress,
            final Member host,
            final List<Position> positions
    ) {
        return Game.builder()
                .gameId(gameEntity.getId())
                .content(gameEntity.getContent())
                .playDate(gameEntity.getPlayDate())
                .playStartTime(gameEntity.getPlayStartTime())
                .playEndTime(gameEntity.getPlayEndTime())
                .playTimeMinutes(gameEntity.getPlayTimeMinutes())
                .mainAddress(gameEntity.getMainAddress())
                .detailAddress(gameEntity.getDetailAddress())
                .latitude(gameEntity.getPoint().getY())
                .longitude(gameEntity.getPoint().getX())
                .status(gameEntity.getStatus())
                .viewCount(gameEntity.getViewCount())
                .cost(gameEntity.getCost())
                .memberCount(gameEntity.getMemberCount())
                .maxMemberCount(gameEntity.getMaxMemberCount())
                .host(host)
                .addressDepth1Name(mainAddress.getAddressDepth1().getName())
                .addressDepth2Name(mainAddress.getAddressDepth2().getName())
                .positions(positions)
                .build();
    }

    public static GameEntity mapNewGameDomainToEntity(
            final NewGame newGame,
            final Point point,
            final MainAddress mainAddress
    ) {
        return GameEntity.builder()
                .content(newGame.getContent())
                .playDate(newGame.getPlayDate())
                .playStartTime(newGame.getPlayStartTime())
                .playEndTime(newGame.getPlayEndTime())
                .playTimeMinutes(newGame.getPlayTimeMinutes())
                .mainAddress(newGame.getMainAddress())
                .detailAddress(newGame.getDetailAddress())
                .cost(newGame.getCost())
                .maxMemberCount(newGame.getMaxMemberCount())
                .hostId(newGame.getHost().getMemberId())
                .point(point)
                .addressDepth1Id(mainAddress.getAddressDepth1().getId())
                .addressDepth2Id(mainAddress.getAddressDepth2().getId())
                .chatRoomId(newGame.getChatRoom().getChatRoomId())
                .build();
    }

    public static List<GamePosition> mapToGamePositionEntities(final List<Position> positions, final Long gameId) {
        return positions.stream()
                .map(position -> GamePosition.builder()
                        .gameId(gameId)
                        .position(position)
                        .build()
                ).toList();
    }

    public static GameMember mapGameMemberEntityToDomain(
            final GameMemberEntity gameMemberEntity,
            final Member member,
            final Game game
    ) {
        return GameMember.builder()
                .gameMemberId(gameMemberEntity.getId())
                .status(gameMemberEntity.getStatus())
                .member(member)
                .game(game)
                .isReview(gameMemberEntity.isReviewDone())
                .build();
    }
}
