package kr.pickple.back.member.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import kr.pickple.back.common.domain.RegistrationStatus;
import kr.pickple.back.game.domain.Game;
import kr.pickple.back.game.domain.GameMember;

@Embeddable
public class MemberGames {

    @OneToMany(mappedBy = "member", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<GameMember> memberGames = new ArrayList<>();

    public RegistrationStatus findRegistrationStatus(final Game game) {
        return memberGames.stream()
                .filter(memberGame -> memberGame.equalsGame(game))
                .findFirst()
                .map(GameMember::getStatus)
                .orElse(RegistrationStatus.NONE);
    }

    public List<Game> getGamesByStatus(final RegistrationStatus status) {
        return memberGames.stream()
                .filter(memberGame -> memberGame.equalsStatus(status))
                .map(GameMember::getGame)
                .toList();
    }

    public List<Game> getCreatedGamesByMember(final Member member) {
        return memberGames.stream()
                .map(GameMember::getGame)
                .filter(game -> game.isHost(member))
                .toList();
    }

    public void addMemberGame(final GameMember memberGame) {
        memberGames.add(memberGame);
    }
}
