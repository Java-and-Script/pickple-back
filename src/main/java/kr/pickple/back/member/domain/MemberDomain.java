package kr.pickple.back.member.domain;

import java.util.List;

import kr.pickple.back.crew.domain.Crew;
import kr.pickple.back.game.domain.Game;
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
public class MemberDomain {

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
    private List<Crew> joinedCrews;
    private List<Game> joinedGames;

    public Boolean isIdMatched(final Long memberId) {
        return this.memberId.equals(memberId);
    }
}
