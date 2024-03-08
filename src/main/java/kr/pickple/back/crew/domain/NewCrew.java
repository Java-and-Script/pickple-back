package kr.pickple.back.crew.domain;

import kr.pickple.back.chat.domain.ChatRoom;
import kr.pickple.back.member.domain.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NewCrew {

    private String name;
    private String content;
    private Integer maxMemberCount;
    private String addressDepth1Name;
    private String addressDepth2Name;
    private String profileImageUrl;
    private String backgroundImageUrl;
    private Member leader;
    private ChatRoom chatRoom;

    @Builder
    private NewCrew(
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

    public void assignImageUrls(final String profileImageUrl, final String backgroundImageUrl) {
        this.profileImageUrl = profileImageUrl;
        this.backgroundImageUrl = backgroundImageUrl;
    }

    public void assignLeader(final Member leader) {
        this.leader = leader;
    }

    public void assignChatRoom(final ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }
}
