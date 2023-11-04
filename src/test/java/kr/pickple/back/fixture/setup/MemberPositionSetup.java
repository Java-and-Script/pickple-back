package kr.pickple.back.fixture.setup;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import kr.pickple.back.member.domain.MemberPosition;
import kr.pickple.back.member.repository.MemberPositionRepository;

@Component
public class MemberPositionSetup {

    @Autowired
    private MemberPositionRepository memberPositionRepository;

    public List<MemberPosition> save(List<MemberPosition> memberPositions) {
        return memberPositionRepository.saveAll(memberPositions);
    }
}
