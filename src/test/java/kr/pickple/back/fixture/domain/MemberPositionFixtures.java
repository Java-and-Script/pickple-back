package kr.pickple.back.fixture.domain;

import java.util.List;

import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.domain.MemberPosition;
import kr.pickple.back.position.domain.Position;

public class MemberPositionFixtures {

    public static MemberPosition memberPositionBuild(
            final Member member,
            final Position position
    ) {
        return MemberPosition.builder()
                .member(member)
                .position(position)
                .build();
    }

    public static List<MemberPosition> memberPositionsBuild(
            final Member member,
            final List<Position> positions
    ) {
        return positions.stream()
                .map(position ->
                        MemberPosition.builder()
                                .member(member)
                                .position(position)
                                .build()
                )
                .toList();
    }
}
