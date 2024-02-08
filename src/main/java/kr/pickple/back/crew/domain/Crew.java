package kr.pickple.back.crew.domain;

import static kr.pickple.back.crew.domain.CrewStatus.*;
import static kr.pickple.back.crew.exception.CrewExceptionCode.*;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import kr.pickple.back.chat.domain.ChatRoom;
import kr.pickple.back.common.domain.BaseEntity;
import kr.pickple.back.crew.exception.CrewException;
import kr.pickple.back.crew.util.CrewStatusConverter;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Crew extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true, length = 20)
    private String name;

    @Column(length = 1000)
    private String content;

    @NotNull
    private Integer memberCount = 1;

    @NotNull
    @Column(length = 300)
    private String profileImageUrl;

    @NotNull
    @Column(length = 300)
    private String backgroundImageUrl;

    @NotNull
    @Column(length = 10)
    @Convert(converter = CrewStatusConverter.class)
    private CrewStatus status = OPEN;

    @NotNull
    private Integer likeCount = 0;

    @NotNull
    private Integer maxMemberCount = 1;

    @NotNull
    private Integer competitionPoint = 0;

    @NotNull
    private Long leaderId;

    @NotNull
    private Long addressDepth1Id;

    @NotNull
    private Long addressDepth2Id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @Builder
    private Crew(
            final String name,
            final String content,
            final String profileImageUrl,
            final String backgroundImageUrl,
            final Integer maxMemberCount,
            final Long leaderId,
            final Long addressDepth1Id,
            final Long addressDepth2Id
    ) {
        this.name = name;
        this.content = content;
        this.profileImageUrl = profileImageUrl;
        this.backgroundImageUrl = backgroundImageUrl;
        this.maxMemberCount = maxMemberCount;
        this.leaderId = leaderId;
        this.addressDepth1Id = addressDepth1Id;
        this.addressDepth2Id = addressDepth2Id;

        updateStatusIfCrewMemberFull();
    }

    public void increaseMemberCount() {
        validateCrewIsClosedOrFull();

        this.memberCount++;

        updateStatusIfCrewMemberFull();
    }

    private void updateStatusIfCrewMemberFull() {
        if (isFullCrew()) {
            this.status = CLOSED;
        }
    }

    private void validateCrewIsClosedOrFull() {
        validateCrewClosed();
        validateCrewFull();
    }

    private void validateCrewClosed() {
        if (isClosedCrew()) {
            throw new CrewException(CREW_STATUS_IS_CLOSED, status);
        }
    }

    private Boolean isClosedCrew() {
        return status == CLOSED;
    }

    private void validateCrewFull() {
        if (isFullCrew()) {
            throw new CrewException(CREW_CAPACITY_LIMIT_REACHED, memberCount);
        }
    }

    private Boolean isFullCrew() {
        return memberCount.equals(maxMemberCount);
    }

    public Boolean isLeader(final Long memberId) {
        return memberId.equals(leaderId);
    }

    public void makeNewCrewChatRoom(final ChatRoom chatRoom) {
        chatRoom.updateMaxMemberCount(maxMemberCount);
        this.chatRoom = chatRoom;
    }
}
