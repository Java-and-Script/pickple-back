package kr.pickple.back.fixture.setup;

import static kr.pickple.back.common.domain.RegistrationStatus.*;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import kr.pickple.back.address.domain.AddressDepth1;
import kr.pickple.back.address.domain.AddressDepth2;
import kr.pickple.back.fixture.domain.GameFixtures;
import kr.pickple.back.game.domain.Game;
import kr.pickple.back.game.domain.GameMember;
import kr.pickple.back.game.repository.GameRepository;
import kr.pickple.back.member.domain.Member;

@Component
public class GameSetup {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private MemberSetup memberSetup;

    @Autowired
    private AddressSetup addressSetup;

    public Game save(final Member host) {
        final AddressDepth1 addressDepth1 = addressSetup.findAddressDepth1("서울시");
        final AddressDepth2 addressDepth2 = addressSetup.findAddressDepth2("영등포구");

        final Game game = GameFixtures.gameBuild(
                addressDepth1,
                addressDepth2,
                host
        );

        game.addGameMember(host);

        final GameMember gameHost = game.getGameMembers().get(0);
        gameHost.updateStatus(CONFIRMED);

        gameRepository.save(game);

        return game;
    }

    public Game saveWithWaitingMembers(final Integer memberCount) {
        final List<Member> members = memberSetup.save(memberCount);
        final Game game = save(members.get(0));
        final List<Member> guests = members.subList(1, members.size());

        guests.forEach(game::addGameMember);

        return game;
    }

    public Game saveWithConfirmedMembers(final Integer memberCount) {
        final Game game = saveWithWaitingMembers(memberCount);
        final List<GameMember> gameMembers = game.getGameMembers();

        gameMembers.forEach(gameMember -> gameMember.updateStatus(CONFIRMED));

        return game;
    }
}
