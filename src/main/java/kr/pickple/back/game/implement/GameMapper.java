package kr.pickple.back.game.implement;

import java.util.List;

import org.locationtech.jts.geom.Point;

import kr.pickple.back.address.dto.response.MainAddress;
import kr.pickple.back.chat.domain.ChatRoom;
import kr.pickple.back.game.domain.GameDomain;
import kr.pickple.back.game.domain.NewGame;
import kr.pickple.back.game.repository.entity.GameEntity;
import kr.pickple.back.game.repository.entity.GamePosition;
import kr.pickple.back.member.domain.MemberDomain;
import kr.pickple.back.member.domain.MemberPosition;
import kr.pickple.back.position.domain.Position;

public class GameMapper {

    public static GameDomain mapToGameDomain(
            final GameEntity gameEntity,
            final MainAddress mainAddress,
            final MemberDomain host,
            final ChatRoom chatRoom,
            final List<Position> positions
    ) {
        return GameDomain.builder()
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
                .chatRoom(chatRoom)
                .build();
    }

    public static GameEntity mapNewGameDomainToEntity(NewGame newGame, Point point, MainAddress mainAddress) {
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
                .chatRoomId(newGame.getChatRoom().getId())
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

    public static List<MemberPosition> mapToMemberPositionEntities(
            final List<Position> positions,
            final Long memberId
    ) {
        return positions.stream()
                .map(position -> MemberPosition.builder()
                        .memberId(memberId)
                        .position(position)
                        .build()
                ).toList();
    }
}
