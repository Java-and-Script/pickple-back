package kr.pickple.back.fixture.setup;

import kr.pickple.back.address.repository.entity.AddressDepth1Entity;
import kr.pickple.back.address.repository.entity.AddressDepth2Entity;
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
        final AddressDepth1Entity addressDepth1 = addressSetup.findAddressDepth1("서울시");
        final AddressDepth2Entity addressDepth2 = addressSetup.findAddressDepth2("영등포구");

        final MemberEntity member = MemberFixtures.memberBuild(
                addressDepth1,
                addressDepth2
        );
        return memberRepository.save(member);
    }

    public List<MemberEntity> save(final int count) {
        final AddressDepth1Entity addressDepth1 = addressSetup.findAddressDepth1("서울시");
        final AddressDepth2Entity addressDepth2 = addressSetup.findAddressDepth2("영등포구");

        final List<MemberEntity> members = MemberFixtures.membersBuild(count, addressDepth1, addressDepth2);

        return members.stream()
                .map(memberRepository::save)
                .toList();
    }
}
