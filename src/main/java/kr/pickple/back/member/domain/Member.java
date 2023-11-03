package kr.pickple.back.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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
import kr.pickple.back.address.domain.AddressDepth1;
import kr.pickple.back.address.domain.AddressDepth2;
import kr.pickple.back.auth.domain.oauth.OauthProvider;
import kr.pickple.back.common.domain.BaseEntity;
import kr.pickple.back.member.util.MemberStatusConverter;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true, length = 100)
    private String email;

    @NotNull
    @Column(unique = true, length = 20)
    private String nickname;

    @Column(length = 1000)
    private String introduction;

    @NotNull
    @Column(length = 300)
    private String profileImageUrl;

    @NotNull
    @Convert(converter = MemberStatusConverter.class)
    @Column(length = 10)
    private MemberStatus status;

    @NotNull
    private Integer mannerScore = 0;

    @NotNull
    private Integer mannerScoreCount = 0;

    @NotNull
    @Column(unique = true)
    private Long oauthId;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private OauthProvider oauthProvider;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_depth1_id")
    private AddressDepth1 addressDepth1;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_depth2_id")
    private AddressDepth2 addressDepth2;

    @Builder
    private Member(
            final String email,
            final String nickname,
            final String profileImageUrl,
            final MemberStatus status,
            final Long oauthId,
            final OauthProvider oauthProvider,
            final AddressDepth1 addressDepth1,
            final AddressDepth2 addressDepth2
    ) {
        this.email = email;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.status = status;
        this.oauthId = oauthId;
        this.oauthProvider = oauthProvider;
        this.addressDepth1 = addressDepth1;
        this.addressDepth2 = addressDepth2;
    }
}
