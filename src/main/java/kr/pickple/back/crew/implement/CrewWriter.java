package kr.pickple.back.crew.implement;

import static kr.pickple.back.crew.exception.CrewExceptionCode.*;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.address.implement.AddressReader;
import kr.pickple.back.crew.repository.entity.CrewEntity;
import kr.pickple.back.crew.domain.Crew;
import kr.pickple.back.crew.domain.CrewMember;
import kr.pickple.back.crew.domain.NewCrew;
import kr.pickple.back.crew.exception.CrewException;
import kr.pickple.back.crew.repository.CrewMemberRepository;
import kr.pickple.back.crew.repository.CrewRepository;
import kr.pickple.back.member.domain.MemberDomain;
import lombok.RequiredArgsConstructor;

@Component
@Transactional
@RequiredArgsConstructor
public class CrewWriter {

    private final AddressReader addressReader;
    private final CrewMapper crewMapper;
    private final CrewRepository crewRepository;
    private final CrewMemberRepository crewMemberRepository;

    public Crew create(final NewCrew newCrew) {
        if (crewRepository.existsByName(newCrew.getName())) {
            throw new CrewException(CREW_IS_EXISTED, newCrew.getName());
        }

        final CrewEntity crewEntity = crewRepository.save(crewMapper.mapNewCrewDomainToEntity(newCrew));

        return crewMapper.mapCrewEntityToDomain(crewEntity);
    }

    public void register(final MemberDomain member, final Crew crew) {
        final CrewMember crewMember = CrewMember.builder()
                .memberId(member.getMemberId())
                .crewId(crew.getCrewId())
                .build();

        crewMember.confirmRegistration();
        crewMemberRepository.save(crewMapper.mapCrewMemberDomainToEntity(crewMember));

        crew.addMember(member);
        crew.increaseMemberCount();
        crewRepository.updateMemberCountAndStatus(crew.getCrewId(), crew.getMemberCount(), crew.getStatus());
    }
}
