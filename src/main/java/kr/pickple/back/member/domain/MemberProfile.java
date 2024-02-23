package kr.pickple.back.member.domain;

import java.util.List;

import kr.pickple.back.crew.domain.CrewDomain;
import kr.pickple.back.position.domain.Position;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberProfile {

    private Long memberId;
    private String email;
    private String nickname;
    private String introduction;
    private String profileImageUrl;
    private Integer mannerScore;
    private Integer mannerScoreCount;
    private String addressDepth1Name;
    private String addressDepth2Name;
    private List<Position> positions;
    private List<CrewDomain> joinedCrews;

    public void updateJoinedCrews(final List<CrewDomain> joinedCrews) {
        this.joinedCrews = joinedCrews;
    }
}
