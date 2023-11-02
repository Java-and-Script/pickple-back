package kr.pickple.back.crew.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import kr.pickple.back.address.domain.AddressDepth1;
import kr.pickple.back.address.domain.AddressDepth2;
import kr.pickple.back.common.domain.BaseEntity;
import kr.pickple.back.crew.util.CrewStatusConverter;
import kr.pickple.back.member.domain.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static kr.pickple.back.crew.domain.CrewStatus.CLOSED;
import static kr.pickple.back.crew.domain.CrewStatus.OPEN;

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

    @Builder
    private Crew(
            final String name,
            final String content,
            final Integer memberCount,
            final String profileImageUrl,
            final String backgroundImageUrl,
            final CrewStatus status,
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
        this.status = validateCrewStatus(maxMemberCount);
        this.maxMemberCount = maxMemberCount;
        this.leader = leader;
        this.addressDepth1 = addressDepth1;
        this.addressDepth2 = addressDepth2;
    }

    public CrewStatus validateCrewStatus(final Integer maxMemberCount) {
        if (memberCount == maxMemberCount) {
            status = CLOSED;
        }
        return this.status;
    }
}
