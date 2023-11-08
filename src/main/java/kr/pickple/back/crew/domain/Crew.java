package kr.pickple.back.crew.domain;

import static kr.pickple.back.crew.domain.CrewStatus.*;
import static kr.pickple.back.crew.exception.CrewExceptionCode.*;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import kr.pickple.back.address.domain.AddressDepth1;
import kr.pickple.back.address.domain.AddressDepth2;
import kr.pickple.back.common.domain.BaseEntity;
import kr.pickple.back.common.domain.RegistrationStatus;
import kr.pickple.back.crew.exception.CrewException;
import kr.pickple.back.crew.util.CrewStatusConverter;
import kr.pickple.back.member.domain.Member;
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

    @Builder
    private Crew(
            final String name,
            final String content,
            final Integer memberCount,
            final String profileImageUrl,
            final String backgroundImageUrl,
            final Integer likeCount,
            final Integer maxMemberCount,
            final Integer competitionPoint,
            final Member leader,
            final AddressDepth1 addressDepth1,
            final AddressDepth2 addressDepth2
    ) {
        this.name = name;
        this.content = content;
        this.profileImageUrl = profileImageUrl;
        this.backgroundImageUrl = backgroundImageUrl;
        this.status = updateStatusWhenMaxMembers(maxMemberCount);
        this.maxMemberCount = maxMemberCount;
        this.leader = leader;
        this.addressDepth1 = addressDepth1;
        this.addressDepth2 = addressDepth2;
    }

    public CrewStatus updateStatusWhenMaxMembers(final Integer maxMemberCount) {
        if (memberCount == maxMemberCount) {
            status = CLOSED;
        }

        return this.status;
    }

    public List<Member> getCrewMembers(final RegistrationStatus status) {
        return crewMembers.getCrewMembers(status);
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
        if (memberCount == maxMemberCount) {
            this.status = CLOSED;
        }
    }

    private void validateCrewIsClosedOrFull() {
        validateCrewClosed();
        validateCrewFull();
    }

    private void validateCrewClosed() {
        if (status == CLOSED) {
            throw new CrewException(CREW_STATUS_IS_CLOSED, status);
        }
    }

    private void validateCrewFull() {
        if (memberCount == maxMemberCount) {
            throw new CrewException(CREW_CAPACITY_LIMIT_REACHED, memberCount);
        }
    }

    public Boolean isLeader(final Member member) {
        return member.equals(leader);
    }
}
