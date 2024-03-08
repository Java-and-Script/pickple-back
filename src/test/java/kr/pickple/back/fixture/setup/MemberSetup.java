package kr.pickple.back.fixture.setup;

import kr.pickple.back.address.domain.AddressDepth1;
import kr.pickple.back.address.domain.AddressDepth2;
import kr.pickple.back.fixture.domain.MemberFixtures;
import kr.pickple.back.member.repository.entity.MemberEntity;
import kr.pickple.back.member.repository.MemberRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MemberSetup {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AddressSetup addressSetup;

    public MemberEntity save() {
        final AddressDepth1 addressDepth1 = addressSetup.findAddressDepth1("서울시");
        final AddressDepth2 addressDepth2 = addressSetup.findAddressDepth2("영등포구");

        final MemberEntity member = MemberFixtures.memberBuild(
                addressDepth1,
                addressDepth2
        );
        return memberRepository.save(member);
    }

    public List<MemberEntity> save(final int count) {
        final AddressDepth1 addressDepth1 = addressSetup.findAddressDepth1("서울시");
        final AddressDepth2 addressDepth2 = addressSetup.findAddressDepth2("영등포구");

        final List<MemberEntity> members = MemberFixtures.membersBuild(count, addressDepth1, addressDepth2);

        return members.stream()
                .map(memberRepository::save)
                .toList();
    }
}
