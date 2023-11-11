package kr.pickple.back.fixture.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import kr.pickple.back.address.domain.AddressDepth1;
import kr.pickple.back.address.domain.AddressDepth2;
import kr.pickple.back.crew.domain.Crew;
import kr.pickple.back.member.domain.Member;

public class CrewFixtures {

    public static Crew crewBuild(
            final AddressDepth1 addressDepth1,
            final AddressDepth2 addressDepth2,
            final Member leaader
    ) {
        return Crew.builder()
                .name("백둥크루1")
                .content("안녕하세요 백둥크루1 입니다.")
                .profileImageUrl("https://amazon.profileimage/1")
                .backgroundImageUrl("https://amazon.backgroundimage/1")
                .maxMemberCount(15)
                .leader(leaader)
                .addressDepth1(addressDepth1)
                .addressDepth2(addressDepth2)
                .build();
    }

    public static List<Crew> crewsBuild(
            final Integer count,
            final AddressDepth1 addressDepth1,
            final AddressDepth2 addressDepth2,
            final Member leader
    ) {
        final List<Crew> crews = new ArrayList<>();

        IntStream.range(0, count).forEach(i -> {
            crews.add(
                    Crew.builder()
                            .name(String.format("백둥크루%d", i))
                            .content(String.format("안녕하세요 백둥크루%d 입니다.", i))
                            .profileImageUrl(String.format("https://amazon.profileimage/%d", i))
                            .backgroundImageUrl(String.format("https://amazon.backgroundimage/%d", i))
                            .maxMemberCount(15)
                            .leader(leader)
                            .addressDepth1(addressDepth1)
                            .addressDepth2(addressDepth2)
                            .build()
            );
        });

        return crews;
    }
}
