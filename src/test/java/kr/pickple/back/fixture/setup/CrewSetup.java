package kr.pickple.back.fixture.setup;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import kr.pickple.back.address.domain.AddressDepth1;
import kr.pickple.back.address.domain.AddressDepth2;
import kr.pickple.back.crew.domain.Crew;
import kr.pickple.back.crew.repository.CrewRepository;
import kr.pickple.back.fixture.domain.CrewFixtures;
import kr.pickple.back.member.domain.Member;

@Component
public class CrewSetup {

    @Autowired
    private CrewRepository crewRepository;

    @Autowired
    private AddressSetup addressSetup;

    public Crew save(final Member member) {
        final AddressDepth1 addressDepth1 = addressSetup.findAddressDepth1("서울시");
        final AddressDepth2 addressDepth2 = addressSetup.findAddressDepth2("영등포구");

        final Crew crew = CrewFixtures.crewBuild(addressDepth1, addressDepth2, member);
        crew.addCrewMember(member);

        return crewRepository.save(crew);
    }

    public List<Crew> save(int count, final Member member) {
        final AddressDepth1 addressDepth1 = addressSetup.findAddressDepth1("서울시");
        final AddressDepth2 addressDepth2 = addressSetup.findAddressDepth2("영등포구");

        final List<Crew> crews = CrewFixtures.crewsBuild(count, addressDepth1, addressDepth2, member);

        return crews.stream()
                .map(crew -> {
                    crew.addCrewMember(member);
                    return crewRepository.save(crew);
                })
                .toList();
    }
}
