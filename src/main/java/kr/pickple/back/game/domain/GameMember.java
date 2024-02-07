package kr.pickple.back.game.domain;

import static java.lang.Boolean.*;
import static kr.pickple.back.common.domain.RegistrationStatus.*;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import kr.pickple.back.chat.domain.ChatRoom;
import kr.pickple.back.common.domain.BaseEntity;
import kr.pickple.back.common.domain.RegistrationStatus;
import kr.pickple.back.common.util.RegistrationStatusAttributeConverter;
import kr.pickple.back.member.domain.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Convert(converter = RegistrationStatusAttributeConverter.class)
    @Column(length = 10)
    private RegistrationStatus status = WAITING;

    @NotNull
    private Boolean isReview = FALSE;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;

    @Builder
    private GameMember(final Member member, final Game game) {
        this.status = getRegistrationStatus(member, game);
        this.member = member;
        this.game = game;
    }

    public void confirmRegistration() {
        this.status = CONFIRMED;
    }

    public void updateStatus(final RegistrationStatus status) {
        if (this.status == WAITING && status == CONFIRMED) {
            game.increaseMemberCount();
        }

        this.status = status;
    }

    public ChatRoom getCrewChatRoom() {
        return game.getChatRoom();
    }

    public Boolean isAlreadyReviewDone() {
        return isReview;
    }

    public void updateReviewDone() {
        this.isReview = TRUE;
    }

    private RegistrationStatus getRegistrationStatus(final Member member, final Game game) {
        final Member host = game.getHost();

        if (member.equals(host)) {
            return CONFIRMED;
        }

        return status;
    }
}
