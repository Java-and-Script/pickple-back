package kr.pickple.back.member.domain;

import static kr.pickple.back.member.exception.MemberExceptionCode.*;

import java.util.List;

import kr.pickple.back.member.exception.MemberException;
import kr.pickple.back.position.domain.Position;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "memberId")
public class Member {

    public static final List<Integer> MANNER_SCORE_POINT_RANGE = List.of(-1, 0, 1);

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

    public Boolean isIdMatched(final Long memberId) {
        return this.memberId.equals(memberId);
    }

    public void updateMannerScore(final Integer mannerScorePoint) {
        if (MANNER_SCORE_POINT_RANGE.contains(mannerScorePoint)) {
            this.mannerScore += mannerScorePoint;
            this.mannerScoreCount += 1;

            return;
        }

        throw new MemberException(MEMBER_UPDATING_MANNER_SCORE_POINT_OUT_OF_RANGE, mannerScorePoint);
    }
}
