package kr.pickple.back.member.domain;

import org.hibernate.annotations.ColumnDefault;

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
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String email;

    @NotNull
    private String nickname;

    @NotNull
    private String introduction;

    @NotNull
    private String profileImageUrl;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private MemberStatus status;

    @NotNull
    @ColumnDefault(value = "0")
    private Integer mannerScore = 0;

    @NotNull
    @ColumnDefault(value = "0")
    private Integer mannerScoreCount = 0;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_depth_1_id")
    private AddressDepth1 addressDepth1;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_depth_2_id")
    private AddressDepth2 addressDepth2;
}
