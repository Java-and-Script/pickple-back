package kr.pickple.back.crew.service;

import kr.pickple.back.common.domain.RegistrationStatus;
import kr.pickple.back.crew.domain.Crew;
import kr.pickple.back.crew.domain.CrewMember;
import kr.pickple.back.crew.dto.request.CrewApplyRequest;
import kr.pickple.back.crew.dto.request.CrewMemberPermitRequest;
import kr.pickple.back.crew.dto.response.CrewProfileResponse;
import kr.pickple.back.crew.exception.CrewException;
import kr.pickple.back.crew.repository.CrewMemberRepository;
import kr.pickple.back.crew.repository.CrewRepository;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.dto.response.MemberResponse;
import kr.pickple.back.member.exception.MemberException;
import kr.pickple.back.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static kr.pickple.back.common.domain.RegistrationStatus.WAITING;
import static kr.pickple.back.crew.exception.CrewExceptionCode.*;
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

        crew.getCrewMembers().addCrewMember(crew, member);
    }

    public CrewProfileResponse findAllCrewMembers(final Long crewId, final RegistrationStatus status) {
        final Crew crew = findByExistCrew(crewId);
        //TODO: 조회하는 사람이 크루장인지 검증 로직 추가(11월 7일,소재훈)

        final List<Member> members = crew.getCrewMembers().getCrewMembers(status);
        final List<MemberResponse> crewMemberResponses = members.stream()
                .map(MemberResponse::from)
                .toList();

        return CrewProfileResponse.fromEntity(crew, crewMemberResponses);
    }

    @Transactional
    public void permitCrewMemberShip(final Long crewId, final Long memberId, final CrewMemberPermitRequest crewMemberPermitRequest) {
        final Crew crew = findByExistCrew(crewId);
        final Member member = findMemberById(memberId);
        //TODO:추후, 크루장인지 검증 로직 추가(11월 2일, 소재훈)

        validateCrewMembership(member, crew, WAITING);

        final CrewMember crewMember = crewMemberRepository.findByMemberAndCrew(member, crew).get();
        crewMember.permitStatus(RegistrationStatus.from(crewMemberPermitRequest.getStatus()));
    }

    private void validateCrewMembership(final Member member, final Crew crew, final RegistrationStatus registrationStatus) {
        final Optional<CrewMember> validateCrewMember = crewMemberRepository.findByMemberAndCrew(member, crew);

        if (!validateCrewMember.isPresent()) {
            throw new CrewException(CREW_MEMBER_STATUS_NOT_FOUND, member.getId());
        }

        final CrewMember crewMember = validateCrewMember.get();

        if (crewMember.getStatus() != registrationStatus) {
            throw new CrewException(CREW_MEMBER_ALREADY_EXISTED, member.getId());
        }
    }

    private Crew findByExistCrew(final Long crewId) {
        return crewRepository.findById(crewId)
                .orElseThrow(() -> new CrewException(CREW_NOT_FOUND, crewId));
    }

    private Member findMemberById(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND, memberId));
    }
}
