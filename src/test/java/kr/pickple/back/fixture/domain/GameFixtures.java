package kr.pickple.back.fixture.domain;

import static kr.pickple.back.chat.domain.RoomType.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

import kr.pickple.back.address.domain.AddressDepth1;
import kr.pickple.back.address.domain.AddressDepth2;
import kr.pickple.back.chat.repository.entity.ChatRoomEntity;
import kr.pickple.back.game.domain.Game;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.position.domain.Position;

public class GameFixtures {

    public static Game gameBuild(
            final AddressDepth1 addressDepth1,
            final AddressDepth2 addressDepth2,
            final Member host
    ) {
        final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        final Point point = geometryFactory.createPoint(new Coordinate(37.125, 126.75));

        return Game.builder()
                .content("하이하이 즐겜 한 판해요")
                .playDate(LocalDate.now().minusDays(1))
                .playStartTime(LocalTime.of(11, 30))
                .playEndTime(LocalTime.of(13, 0))
                .playTimeMinutes(90)
                .mainAddress("서울 영등포구 도림동 254")
                .detailAddress("영등포 다목적 체육관 2층 201호")
                .cost(100)
                .maxMemberCount(5)
                .point(point)
                .host(host)
                .addressDepth1(addressDepth1)
                .addressDepth2(addressDepth2)
                .positions(List.of(Position.CENTER, Position.POINT_GUARD))
                .build();
    }

    public static ChatRoomEntity gameChatRoomBuild() {
        return ChatRoomEntity.builder()
                .name("11.10 영등포구")
                .type(GAME)
                .build();
    }
}
