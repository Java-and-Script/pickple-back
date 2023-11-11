package kr.pickple.back.fixture.setup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import kr.pickple.back.address.domain.AddressDepth1;
import kr.pickple.back.address.domain.AddressDepth2;
import kr.pickple.back.fixture.domain.GameFixtures;
import kr.pickple.back.game.domain.Game;
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

    public Game save() {
        final AddressDepth1 addressDepth1 = addressSetup.findAddressDepth1("서울시");
        final AddressDepth2 addressDepth2 = addressSetup.findAddressDepth2("영등포구");
        final Member host = memberSetup.save();

        final Game game = GameFixtures.gameBuild(
                addressDepth1,
                addressDepth2,
                host
        );

        return gameRepository.save(game);
    }
}
