package kr.pickple.back.crew.service;

import kr.pickple.back.crew.domain.Crew;
import kr.pickple.back.crew.domain.CrewMember;
import kr.pickple.back.crew.dto.CrewMemberRelationDto;
import kr.pickple.back.crew.dto.request.CrewApplyRequest;
import kr.pickple.back.crew.dto.response.CrewProfileResponse;
import kr.pickple.back.crew.exception.CrewException;
import kr.pickple.back.crew.exception.CrewExceptionCode;
import kr.pickple.back.crew.repository.CrewMemberRepository;
import kr.pickple.back.crew.repository.CrewRepository;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.exception.MemberException;
import kr.pickple.back.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static kr.pickple.back.common.domain.RegistrationStatus.CONFIRMED;
import static kr.pickple.back.common.domain.RegistrationStatus.WAITING;
import static kr.pickple.back.crew.exception.CrewExceptionCode.CREW_MEMBER_ALREADY_EXISTED;
import static kr.pickple.back.crew.exception.CrewExceptionCode.CREW_NOT_FOUND;
import static kr.pickple.back.member.exception.MemberExceptionCode.MEMBER_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CrewMemberService {

    private final CrewRepository crewRepository;
    private final MemberRepository memberRepository;
    private final CrewMemberRepository crewMemberRepository;

    @Transactional
    public void applyForCrewMemberShip(final Long crewId, final CrewApplyRequest crewApplyRequest) {
        final Crew crew = findByExistCrew(crewId);
        final Member member = findMemberById(crewApplyRequest.getMemberId());

        validateExistCrewMember(member, crew);

        final CrewMember crewMember = CrewMember.builder()
                .crew(crew)
                .member(member)
                .status(WAITING)
                .build();
        crewMemberRepository.save(crewMember);
    }

    public CrewProfileResponse findAllApplyForCrewMemberShip(final Long crewId, final String status) {
        final Crew crew = findByExistCrew(crewId);
        final List<CrewMember> crewMemberList;
        //TODO:추후, 크루장인지 검증 로직 추가(11월 2일,소재훈)

        if (status.equals(CONFIRMED.getDescription())) {
            throw new CrewException(CrewExceptionCode.CREW_MEMBER_ALREADY_EXISTED);
        }

        crewMemberList = crewMemberRepository.findCrewMemberByStatusAndCrewId(WAITING, crewId);

        final List<CrewMemberRelationDto> crewMemberRelationDtoList = crewMemberList.stream()
                .map(CrewMemberRelationDto::fromEntity)
                .collect(Collectors.toList());

        return CrewProfileResponse.fromEntity(crew, crewMemberRelationDtoList);
    }

    private Crew findByExistCrew(final Long crewId) {
        return crewRepository.findById(crewId)
                .orElseThrow(() -> new CrewException(CREW_NOT_FOUND, crewId));
    }

    private void validateExistCrewMember(final Member member, final Crew crew) {
        final Optional<CrewMember> crewMember = crewMemberRepository.findByMemberAndCrew(member, crew);

        if (crewMember.isPresent()) {
            throw new CrewException(CREW_MEMBER_ALREADY_EXISTED, member.getId());
        }
    }

    private Member findMemberById(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND, memberId));
    }
}
