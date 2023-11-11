package kr.pickple.back.fixture.domain;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import kr.pickple.back.address.domain.AddressDepth1;
import kr.pickple.back.address.domain.AddressDepth2;
import kr.pickple.back.game.domain.Game;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.position.domain.Position;

public class GameFixtures {

    public static Game gameBuild(
            final AddressDepth1 addressDepth1,
            final AddressDepth2 addressDepth2,
            final Member host
    ) {
        return Game.builder()
                .content("하이하이 즐겜 한 판해요")
                .playDate(LocalDate.of(2023, 11, 10))
                .playStartTime(LocalTime.of(11, 30))
                .playEndTime(LocalTime.of(13, 0))
                .playTimeMinutes(90)
                .mainAddress("서울 영등포구 도림동 254")
                .detailAddress("영등포 다목적 체육관 2층 201호")
                .cost(100)
                .maxMemberCount(5)
                .host(host)
                .addressDepth1(addressDepth1)
                .addressDepth2(addressDepth2)
                .positions(List.of(Position.CENTER, Position.POINT_GUARD))
                .build();
    }
}
