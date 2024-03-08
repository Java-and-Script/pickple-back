package kr.pickple.back.crew.domain;

import static kr.pickple.back.crew.domain.CrewStatus.*;
import static kr.pickple.back.crew.exception.CrewExceptionCode.*;

import kr.pickple.back.chat.domain.ChatRoom;
import kr.pickple.back.crew.exception.CrewException;
import kr.pickple.back.member.domain.Member;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Crew {

    private Long crewId;
    private String name;
    private String content;
    private Integer memberCount;
    private Integer maxMemberCount;
    private CrewStatus status = OPEN;
    private Member leader;
    private String addressDepth1Name;
    private String addressDepth2Name;
    private String profileImageUrl;
    private String backgroundImageUrl;
    private Integer likeCount;
    private Integer competitionPoint;
    private ChatRoom chatRoom;

    public void increaseMemberCount() {
        if (status == CLOSED) {
            throw new CrewException(CREW_STATUS_IS_CLOSED, status);
        }

        if (memberCount.equals(maxMemberCount)) {
            throw new CrewException(CREW_CAPACITY_LIMIT_REACHED, memberCount);
        }

        memberCount += 1;

        if (memberCount.equals(maxMemberCount)) {
            this.status = CLOSED;
        }
    }

    public Boolean isLeader(final Long memberId) {
        return leader.isIdMatched(memberId);
    }
}
