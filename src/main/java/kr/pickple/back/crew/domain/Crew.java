package kr.pickple.back.crew.domain;

import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import kr.pickple.back.common.domain.AddressDepth1;
import kr.pickple.back.common.domain.AddressDepth2;
import kr.pickple.back.common.domain.BaseEntity;
import kr.pickple.back.member.domain.Member;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

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
    @ColumnDefault(value = "1")
    private Integer memberCount = 1;

    @NotNull
    @Column(length = 300)
    private String profileImageUrl;

    @NotNull
    @Column(length = 300)
    private String backgroundImageUrl;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    @Column(length = 10)
    private CrewStatus status;

    @NotNull
    @ColumnDefault(value = "0")
    private Integer likeCount = 0;

    @NotNull
    @ColumnDefault(value = "1")
    private Integer maxMemberCount = 1;

    @NotNull
    @ColumnDefault(value = "0")
    private Integer competitionPoint = 0;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_id")
    private Member leader;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_depth_1_id")
    private AddressDepth1 addressDepth1;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_depth_2_id")
    private AddressDepth2 addressDepth2;
}
