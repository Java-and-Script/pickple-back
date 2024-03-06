package kr.pickple.back.game.domain;

import kr.pickple.back.common.domain.RegistrationStatus;
import kr.pickple.back.member.domain.MemberDomain;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GameMember {

    private Long gameMemberId;
    private RegistrationStatus status;
    private MemberDomain member;
    private GameDomain game;

    public void updateGameMemberId(final Long gameMemberId) {
        this.gameMemberId = gameMemberId;
    }

    public void updateRegistrationStatus(final RegistrationStatus status) {
        this.status = status;
    }
}
