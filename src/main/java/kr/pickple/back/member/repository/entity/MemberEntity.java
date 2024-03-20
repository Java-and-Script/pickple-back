package kr.pickple.back.member.repository.entity;

import static kr.pickple.back.member.exception.MemberExceptionCode.*;

import java.text.MessageFormat;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import kr.pickple.back.auth.domain.oauth.OauthProvider;
import kr.pickple.back.common.domain.BaseEntity;
import kr.pickple.back.member.domain.MemberStatus;
import kr.pickple.back.member.exception.MemberException;
import kr.pickple.back.member.util.MemberStatusConverter;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
public class MemberEntity extends BaseEntity {

    public static final List<Integer> MANNER_SCORE_POINT_RANGE = List.of(-1, 0, 1);

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
    private Long addressDepth1Id;

    @NotNull
    private Long addressDepth2Id;

    @Builder
    private MemberEntity(
            final String email,
            final String nickname,
            final String profileImageUrl,
            final MemberStatus status,
            final Long oauthId,
            final OauthProvider oauthProvider,
            final Long addressDepth1Id,
            final Long addressDepth2Id
    ) {
        this.email = email;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.status = status;
        this.oauthId = oauthId;
        this.oauthProvider = oauthProvider;
        this.addressDepth1Id = addressDepth1Id;
        this.addressDepth2Id = addressDepth2Id;

        setDefaultIntroduction(nickname);
    }

    private void setDefaultIntroduction(final String nickname) {
        this.introduction = MessageFormat.format("안녕하세요. {0}입니다.", nickname);
    }

    public void updateMannerScore(final Integer mannerScorePoint) {
        if (MANNER_SCORE_POINT_RANGE.contains(mannerScorePoint)) {
            this.mannerScore += mannerScorePoint;
            this.mannerScoreCount += 1;

            return;
        }

        throw new MemberException(MEMBER_UPDATING_MANNER_SCORE_POINT_OUT_OF_RANGE, mannerScorePoint);
    }
}
