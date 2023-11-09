package kr.pickple.back.crew.service;

import kr.pickple.back.common.domain.RegistrationStatus;
import kr.pickple.back.crew.domain.Crew;
import kr.pickple.back.crew.domain.CrewMember;
import kr.pickple.back.crew.dto.request.CrewMemberUpdateStatusRequest;
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
    public void applyForCrewMemberShip(final Long crewId, final Long loggedInMemberId) {
        final Crew crew = findByExistCrew(crewId);
        final Member member = findMemberById(loggedInMemberId);

        crew.addCrewMember(member);
    }

    public CrewProfileResponse findAllCrewMembers(final Long loggedInMemberId, final Long crewId, final RegistrationStatus status) {
        final Crew crew = findByExistCrew(crewId);
        if (!crew.isLeader(loggedInMemberId)) {
            throw new CrewException(CREW_IS_NOT_LEADER, loggedInMemberId);
        }

        final List<Member> members = crew.getCrewMembers().getCrewMembers(status);
        final List<MemberResponse> crewMemberResponses = members.stream()
                .map(MemberResponse::from)
                .toList();

        return CrewProfileResponse.of(crew, crewMemberResponses);
    }

    @Transactional
    public void crewMemberStatusUpdate(final Long loggedInMemberId, final Long crewId, final Long memberId,
                                       final CrewMemberUpdateStatusRequest crewMemberUpdateStatusRequest) {
        final CrewMember crewMember = crewMemberRepository.findByMemberIdAndCrewId(memberId, crewId)
                .orElseThrow(() -> new CrewException(CREW_MEMBER_NOT_FOUND, memberId, crewId));

        final Crew crew = crewMember.getCrew();
        if (!crew.isLeader(loggedInMemberId)) {
            throw new CrewException(CREW_IS_NOT_LEADER, loggedInMemberId);
        }

        crewMember.updateStatus(crewMemberUpdateStatusRequest.getStatus());
    }

    @Transactional
    public void deleteMemberShip(final Long loggedInMemberId, final Long crewId, final Long memberId) {
        final CrewMember crewMember = crewMemberRepository.findByMemberIdAndCrewId(memberId, crewId)
                .orElseThrow(() -> new CrewException(CREW_MEMBER_NOT_FOUND, memberId, crewId));

        final Crew crew = crewMember.getCrew();
        if (crew.isLeader(loggedInMemberId)) {
            deleteCrewMemberByLeader(crewMember);
            return;
        }

        if (memberId.equals(loggedInMemberId)) {
            cancelCrewMemberShipByUser(crewMember);
            return;
        }

        throw new CrewException(CREW_MEMBER_NOT_ALLOWED, loggedInMemberId);
    }

    private Crew findByExistCrew(final Long crewId) {
        return crewRepository.findById(crewId)
                .orElseThrow(() -> new CrewException(CREW_NOT_FOUND, crewId));
    }

    private Member findMemberById(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND, memberId));
    }

    private void deleteCrewMemberByLeader(final CrewMember crewMember) {
        crewMemberRepository.delete(crewMember);
    }

    private void cancelCrewMemberShipByUser(final CrewMember crewMember) {
        if (crewMember.getStatus() != WAITING) {
            throw new CrewException(CREW_MEMBER_NOT_APPLIED);
        }

        crewMemberRepository.delete(crewMember);
    }
}
