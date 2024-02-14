package kr.pickple.back.crew.domain;

import kr.pickple.back.chat.domain.ChatRoom;
import kr.pickple.back.member.domain.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
//TODO: CrewDomain -> Crew, CrewMemberDomain -> CrewMember 변경 예정 (2024.02.15 김영주)
public class CrewDomain {

    private Long crewId;
    private String name;
    private String content;
    private Integer maxMemberCount;
    private Member leader;
    private String addressDepth1Name;
    private String addressDepth2Name;
    private String profileImageUrl;
    private String backgroundImageUrl;
    private ChatRoom chatRoom;

    @Builder
    private CrewDomain(
            final String name,
            final String content,
            final Integer maxMemberCount,
            final String addressDepth1Name,
            final String addressDepth2Name
    ) {
        this.name = name;
        this.content = content;
        this.maxMemberCount = maxMemberCount;
        this.addressDepth1Name = addressDepth1Name;
        this.addressDepth2Name = addressDepth2Name;
    }

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
}
