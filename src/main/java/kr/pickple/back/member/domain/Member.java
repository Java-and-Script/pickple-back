package kr.pickple.back.member.domain;

import static kr.pickple.back.member.exception.MemberExceptionCode.*;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import kr.pickple.back.address.domain.AddressDepth1;
import kr.pickple.back.address.domain.AddressDepth2;
import kr.pickple.back.auth.domain.oauth.OauthProvider;
import kr.pickple.back.common.domain.BaseEntity;
import kr.pickple.back.common.domain.RegistrationStatus;
import kr.pickple.back.crew.domain.Crew;
import kr.pickple.back.game.domain.Game;
import kr.pickple.back.member.exception.MemberException;
import kr.pickple.back.member.util.MemberStatusConverter;
import kr.pickple.back.position.domain.Position;
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

    private static final List<Integer> MANNER_SCORE_POINT_RANGE = List.of(-1, 0, 1);

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

    @Embedded
    private MemberCrews memberCrews = new MemberCrews();

    @Embedded
    private MemberGames memberGames = new MemberGames();

    @OneToMany(mappedBy = "member", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<MemberPosition> memberPositions = new ArrayList<>();

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

    public List<Position> getPositions() {
        return memberPositions.stream()
                .map(MemberPosition::getPosition)
                .toList();
    }

    public List<Crew> getCrewsByStatus(RegistrationStatus status) {
        return memberCrews.getCrewsByStatus(status);
    }

    public List<Crew> getCreatedCrews() {
        return memberCrews.getCreatedCrewsByMember(this);
    }

    public List<Game> getGamesByStatus(final RegistrationStatus status) {
        return memberGames.getGamesByStatus(status);
    }

    public List<Game> getCreatedGames() {
        return memberGames.getCreatedGamesByMember(this);
    }

    public void updateMannerScore(final Integer mannerScorePoint) {
        if (MANNER_SCORE_POINT_RANGE.contains(mannerScorePoint)) {
            this.mannerScore += mannerScorePoint;

            return;
        }

        throw new MemberException(MEMBER_UPDATING_MANNER_SCORE_POINT_OUT_OF_RANGE, mannerScorePoint);
    }
}
