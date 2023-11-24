package kr.pickple.back.crew.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import kr.pickple.back.address.domain.AddressDepth1;
import kr.pickple.back.address.domain.AddressDepth2;
import kr.pickple.back.chat.domain.ChatRoom;
import kr.pickple.back.common.domain.BaseEntity;
import kr.pickple.back.common.domain.RegistrationStatus;
import kr.pickple.back.crew.exception.CrewException;
import kr.pickple.back.crew.util.CrewStatusConverter;
import kr.pickple.back.member.domain.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.List;

import static kr.pickple.back.crew.domain.CrewStatus.CLOSED;
import static kr.pickple.back.crew.domain.CrewStatus.OPEN;
import static kr.pickple.back.crew.exception.CrewExceptionCode.CREW_CAPACITY_LIMIT_REACHED;
import static kr.pickple.back.crew.exception.CrewExceptionCode.CREW_STATUS_IS_CLOSED;

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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_id")
    private Member leader;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_depth1_id")
    private AddressDepth1 addressDepth1;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_depth2_id")
    private AddressDepth2 addressDepth2;

    @Embedded
    private CrewMembers crewMembers = new CrewMembers();

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
            final Member leader,
            final AddressDepth1 addressDepth1,
            final AddressDepth2 addressDepth2
    ) {
        this.name = name;
        this.content = getDefaultIfContentIsBlank(content);
        this.profileImageUrl = profileImageUrl;
        this.backgroundImageUrl = backgroundImageUrl;
        this.maxMemberCount = maxMemberCount;
        this.leader = leader;
        this.addressDepth1 = addressDepth1;
        this.addressDepth2 = addressDepth2;
        updateStatusIfCrewMemberFull();
    }

    private String getDefaultIfContentIsBlank(final String content) {
        if (StringUtils.hasText(content)) {
            return content;
        }

        return MessageFormat.format("안녕하세요. {0}입니다.", name);
    }

    public List<Member> getMembersByStatus(final RegistrationStatus status) {
        return crewMembers.getCrewMembers(status);
    }

    public List<CrewMember> getCrewMembers() {
        return crewMembers.getCrewMembers();
    }

    public void addCrewMember(final Member member) {
        crewMembers.addCrewMember(this, member);
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

    public Boolean isLeader(final Member member) {
        return member.equals(leader);
    }

    public Boolean isLeader(final Long memberId) {
        return memberId.equals(leader.getId());
    }

    public void makeNewCrewChatRoom(final ChatRoom chatRoom) {
        chatRoom.updateMaxMemberCount(maxMemberCount);
        this.chatRoom = chatRoom;
    }

    public Boolean isConfirmedCrewMember(final Member member) {
        return crewMembers.isAlreadyConfirmed(member);
    }
}
