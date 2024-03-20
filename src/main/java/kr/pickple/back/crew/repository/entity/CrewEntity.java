package kr.pickple.back.crew.repository.entity;

import static kr.pickple.back.crew.domain.CrewStatus.*;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import kr.pickple.back.common.domain.BaseEntity;
import kr.pickple.back.crew.domain.CrewStatus;
import kr.pickple.back.crew.util.CrewStatusConverter;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "crew")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CrewEntity extends BaseEntity {

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
    @Column(name = "address_depth1_id")
    private Long addressDepth1Id;

    @NotNull
    @Column(name = "address_depth2_id")
    private Long addressDepth2Id;

    private Long chatRoomId;

    @Builder
    private CrewEntity(
            final String name,
            final String content,
            final String profileImageUrl,
            final String backgroundImageUrl,
            final Integer maxMemberCount,
            final Long leaderId,
            final Long addressDepth1Id,
            final Long addressDepth2Id,
            final Long chatRoomId
    ) {
        this.name = name;
        this.content = content;
        this.profileImageUrl = profileImageUrl;
        this.backgroundImageUrl = backgroundImageUrl;
        this.maxMemberCount = maxMemberCount;
        this.leaderId = leaderId;
        this.addressDepth1Id = addressDepth1Id;
        this.addressDepth2Id = addressDepth2Id;
        this.chatRoomId = chatRoomId;
    }

    public Boolean isLeader(final Long memberId) {
        return memberId.equals(leaderId);
    }
}
