package kr.pickple.back.fixture.domain;

import static kr.pickple.back.chat.domain.RoomType.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import kr.pickple.back.address.domain.AddressDepth1;
import kr.pickple.back.address.domain.AddressDepth2;
import kr.pickple.back.chat.repository.entity.ChatRoomEntity;
import kr.pickple.back.crew.repository.entity.CrewEntity;
import kr.pickple.back.crew.repository.entity.CrewMemberEntity;
import kr.pickple.back.member.domain.Member;

public class CrewFixtures {

    public static CrewEntity crewBuild(
            final AddressDepth1 addressDepth1,
            final AddressDepth2 addressDepth2,
            final Member leader
    ) {
        return CrewEntity.builder()
                .name("백둥크루1")
                .content("안녕하세요 백둥크루1 입니다.")
                .profileImageUrl("https://amazon.profileimage/1")
                .backgroundImageUrl("https://amazon.backgroundimage/1")
                .maxMemberCount(15)
                .leader(leader)
                .addressDepth1(addressDepth1)
                .addressDepth2(addressDepth2)
                .build();
    }

    public static CrewMemberEntity crewMemberBuild(final Member member, final CrewEntity crew) {
        return CrewMemberEntity.builder()
                .member(member)
                .crew(crew)
                .build();
    }

    public static ChatRoomEntity crewChatRoomBuild() {
        return ChatRoomEntity.builder()
                .name("백둥크루1")
                .type(CREW)
                .build();
    }

    public static List<CrewEntity> crewsBuild(
            final Integer count,
            final AddressDepth1 addressDepth1,
            final AddressDepth2 addressDepth2,
            final Member leader
    ) {
        final List<CrewEntity> crews = new ArrayList<>();

        IntStream.range(0, count).forEach(i -> {
            crews.add(
                    CrewEntity.builder()
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
