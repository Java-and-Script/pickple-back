package kr.pickple.back.crew.implement;

import static kr.pickple.back.crew.exception.CrewExceptionCode.*;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.crew.domain.Crew;
import kr.pickple.back.crew.domain.CrewDomain;
import kr.pickple.back.crew.domain.CrewMember;
import kr.pickple.back.crew.domain.CrewMemberDomain;
import kr.pickple.back.crew.exception.CrewException;
import kr.pickple.back.crew.repository.CrewMemberRepository;
import kr.pickple.back.crew.repository.CrewRepository;
import kr.pickple.back.member.domain.Member;
import lombok.RequiredArgsConstructor;

@Component
@Transactional
@RequiredArgsConstructor
public class CrewWriter {

    private final CrewMapper crewMapper;
    private final CrewRepository crewRepository;
    private final CrewMemberRepository crewMemberRepository;

    public void create(final CrewDomain crew) {
        if (crewRepository.existsByName(crew.getName())) {
            throw new CrewException(CREW_IS_EXISTED, crew.getName());
        }

        final Crew crewEntity = crewMapper.mapCrewDomainToEntity(crew);
        final Crew savedCrewEntity = crewRepository.save(crewEntity);

        crew.updateCrewId(savedCrewEntity.getId());
    }

    public void register(final Member member, final CrewDomain crew) {
        final CrewMemberDomain crewMember = CrewMemberDomain.builder()
                .memberId(member.getId())
                .crewId(crew.getCrewId())
                .build();

        crewMember.confirmRegistration();
        crew.increaseMemberCount();
        //TODO : crew 인원 + 1 쿼리 추가 예정

        final CrewMember crewMemberEntity = crewMapper.mapCrewMemberDomainToEntity(crewMember);
        final CrewMember savedCrewMemberEntity = crewMemberRepository.save(crewMemberEntity);

        crewMember.updateCrewMemberId(savedCrewMemberEntity.getId());
    }
}
