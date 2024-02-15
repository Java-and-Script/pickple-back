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
//TODO: CrewDomain -> Crew, CrewMemberDomain -> CrewMember 변경 예정 (2024.02.15 김영주)
public class CrewDomain {

    private Long crewId;
    private String name;
    private String content;
    private Integer memberCount = 0;
    private Integer maxMemberCount = 1;
    private CrewStatus status = OPEN;
    private Member leader;
    private String addressDepth1Name;
    private String addressDepth2Name;
    private String profileImageUrl;
    private String backgroundImageUrl;
    private Integer likeCount = 0;
    private Integer competitionPoint = 0;
    private ChatRoom chatRoom;

    public void updateCrewId(final Long crewId) {
        this.crewId = crewId;
    }

    public void updateLeader(final Member leader) {
        this.leader = leader;
    }

    public void updateProfileImageUrl(final String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void updateBackgroundImageUrl(final String backgroundImageUrl) {
        this.backgroundImageUrl = backgroundImageUrl;
    }

    public void updateChatRoom(final ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

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
}
