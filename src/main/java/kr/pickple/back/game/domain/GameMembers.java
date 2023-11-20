package kr.pickple.back.game.domain;

import static kr.pickple.back.game.exception.GameExceptionCode.*;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.BatchSize;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import kr.pickple.back.common.domain.RegistrationStatus;
import kr.pickple.back.game.exception.GameException;
import kr.pickple.back.member.domain.Member;
import lombok.Getter;

@Embeddable
public class GameMembers {

    @Getter
    @BatchSize(size = 1000)
    @OneToMany(mappedBy = "game", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<GameMember> gameMembers = new ArrayList<>();

    public List<Member> getMembersByStatus(final RegistrationStatus status) {
        return gameMembers.stream()
                .filter(gameMember -> gameMember.equalsStatus(status))
                .map(GameMember::getMember)
                .toList();
    }

    public void addGameMember(final Game game, final Member member) {
        validateIsAlreadyRegisteredGameMember(member);

        final GameMember gameMember = buildGameMember(game, member);
        gameMembers.add(gameMember);
    }

    private void validateIsAlreadyRegisteredGameMember(final Member member) {
        if (isAlreadyRegistered(member)) {
            throw new GameException(GAME_MEMBER_IS_EXISTED, member.getId());
        }
    }

    private boolean isAlreadyRegistered(final Member member) {
        return gameMembers.stream()
                .anyMatch(gameMember -> member.equals(gameMember.getMember()));
    }

    private GameMember buildGameMember(final Game game, final Member member) {
        return GameMember.builder()
                .member(member)
                .game(game)
                .build();
    }
}
