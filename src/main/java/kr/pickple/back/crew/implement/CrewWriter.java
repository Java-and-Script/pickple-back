package kr.pickple.back.crew.implement;

import static kr.pickple.back.common.domain.RegistrationStatus.*;
import static kr.pickple.back.crew.exception.CrewExceptionCode.*;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.address.dto.response.MainAddress;
import kr.pickple.back.address.implement.AddressReader;
import kr.pickple.back.common.domain.RegistrationStatus;
import kr.pickple.back.crew.domain.Crew;
import kr.pickple.back.crew.domain.CrewMember;
import kr.pickple.back.crew.domain.NewCrew;
import kr.pickple.back.crew.exception.CrewException;
import kr.pickple.back.crew.repository.CrewMemberRepository;
import kr.pickple.back.crew.repository.CrewRepository;
import kr.pickple.back.crew.repository.entity.CrewEntity;
import kr.pickple.back.crew.repository.entity.CrewMemberEntity;
import kr.pickple.back.member.domain.MemberDomain;
import lombok.RequiredArgsConstructor;

@Component
@Transactional
@RequiredArgsConstructor
public class CrewWriter {

    private final AddressReader addressReader;
    private final CrewRepository crewRepository;
    private final CrewMemberRepository crewMemberRepository;

    public Crew create(final NewCrew newCrew) {
        if (crewRepository.existsByName(newCrew.getName())) {
            throw new CrewException(CREW_IS_EXISTED, newCrew.getName());
        }

        final MainAddress mainAddress = addressReader.readMainAddressByNames(
                newCrew.getAddressDepth1Name(),
                newCrew.getAddressDepth2Name()
        );

        final CrewEntity crewEntity = CrewMapper.mapNewCrewDomainToEntity(newCrew, mainAddress);
        final CrewEntity savedCrewEntity = crewRepository.save(crewEntity);

        return CrewMapper.mapCrewEntityToDomain(
                savedCrewEntity,
                mainAddress,
                newCrew.getLeader(),
                newCrew.getChatRoom()
        );
    }

    public CrewMember register(final MemberDomain member, final Crew crew) {
        final Long memberId = member.getMemberId();
        final Long crewId = crew.getCrewId();

        if (crewMemberRepository.existsByCrewIdAndMemberId(crewId, memberId)) {
            throw new CrewException(CREW_MEMBER_ALREADY_EXISTED, crewId, memberId);
        }

        final CrewMember crewMember = CrewMember.builder()
                .status(WAITING)
                .member(member)
                .crew(crew)
                .build();

        final CrewMemberEntity crewMemberEntity = CrewMapper.mapCrewMemberDomainToEntity(crewMember);
        final CrewMemberEntity savedCrewMemberEntity = crewMemberRepository.save(crewMemberEntity);
        crewMember.updateCrewMemberId(savedCrewMemberEntity.getId());

        return crewMember;
    }

    public void updateMemberRegistrationStatus(final CrewMember crewMember, final RegistrationStatus status) {
        crewMember.updateRegistrationStatus(status);
        crewMemberRepository.updateRegistrationStatus(crewMember.getCrewMemberId(), status);

        final Crew crew = crewMember.getCrew();
        crew.increaseMemberCount();
        crewRepository.updateMemberCountAndStatus(crew.getCrewId(), crew.getMemberCount(), crew.getStatus());
    }

    public void cancel(final CrewMember crewMember) {
        if (crewMember.getStatus() != WAITING) {
            throw new CrewException(CREW_MEMBER_STATUS_IS_NOT_WAITING, crewMember.getCrewMemberId());
        }

        delete(crewMember);
    }

    public void delete(final CrewMember crewMember) {
        crewMemberRepository.deleteById(crewMember.getCrewMemberId());
    }
}
