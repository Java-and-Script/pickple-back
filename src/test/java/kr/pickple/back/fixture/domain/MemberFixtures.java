package kr.pickple.back.fixture.domain;

import kr.pickple.back.address.domain.AddressDepth1;
import kr.pickple.back.address.domain.AddressDepth2;
import kr.pickple.back.auth.domain.oauth.OauthProvider;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.domain.MemberStatus;
import kr.pickple.back.position.domain.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class MemberFixtures {

    public static Member memberBuild(
            final AddressDepth1 addressDepth1,
            final AddressDepth2 addressDepth2
    ) {
        return Member.builder()
                .email("pickple1@pickple.kr")
                .nickname("pickple1")
                .profileImageUrl("https://amazon.image")
                .status(MemberStatus.ACTIVE)
                .oauthId(1L)
                .oauthProvider(OauthProvider.KAKAO)
                .addressDepth1(addressDepth1)
                .addressDepth2(addressDepth2)
                .positions(List.of(Position.CENTER, Position.POINT_GUARD))
                .build();
    }

    public static List<Member> membersBuild(
            final int count,
            final AddressDepth1 addressDepth1,
            final AddressDepth2 addressDepth2
    ) {
        final List<Member> members = new ArrayList<>();

        IntStream.range(0, count).forEach(i -> {
            members.add(
                    Member.builder()
                            .email(String.format("pickple%d@pickple.kr", i))
                            .nickname(String.format("pickple%d", i))
                            .profileImageUrl("https://amazon.image")
                            .status(MemberStatus.ACTIVE)
                            .oauthId((long)i)
                            .oauthProvider(OauthProvider.KAKAO)
                            .addressDepth1(addressDepth1)
                            .addressDepth2(addressDepth2)
                            .positions(List.of(Position.CENTER, Position.POINT_GUARD))
                            .build()
            );
        });

        return members;
    }
}
