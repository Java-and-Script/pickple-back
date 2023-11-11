package kr.pickple.back.crew.service;

import static kr.pickple.back.common.domain.RegistrationStatus.*;
import static kr.pickple.back.crew.exception.CrewExceptionCode.*;
import static kr.pickple.back.member.exception.MemberExceptionCode.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CrewMemberService {

    private final CrewRepository crewRepository;
    private final MemberRepository memberRepository;
    private final CrewMemberRepository crewMemberRepository;

    @Transactional
    public void applyForCrewMemberShip(final Long crewId, final Long loggedInMemberId) {
        final Crew crew = findCrewById(crewId);
        final Member member = findMemberById(loggedInMemberId);

        crew.addCrewMember(member);
    }

    public CrewProfileResponse findAllCrewMembers(
            final Long loggedInMemberId,
            final Long crewId,
            final RegistrationStatus status
    ) {
        final Crew crew = findCrewById(crewId);

        validateIsLeader(loggedInMemberId, crew);

        final List<Member> members = crew.getCrewMembers(status);
        final List<MemberResponse> crewMemberResponses = members.stream()
                .map(MemberResponse::from)
                .toList();

        return CrewProfileResponse.of(crew, crewMemberResponses);
    }

    private Crew findCrewById(final Long crewId) {
        return crewRepository.findById(crewId)
                .orElseThrow(() -> new CrewException(CREW_NOT_FOUND, crewId));
    }

    private Member findMemberById(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND, memberId));
    }

    @Transactional
    public void crewMemberStatusUpdate(
            final Long loggedInMemberId,
            final Long crewId,
            final Long memberId,
            final CrewMemberUpdateStatusRequest crewMemberUpdateStatusRequest
    ) {
        final CrewMember crewMember = findCrewMemberByCrewIdAndMemberId(crewId, memberId);
        final Crew crew = crewMember.getCrew();

        validateIsLeader(loggedInMemberId, crew);

        crewMember.updateStatus(crewMemberUpdateStatusRequest.getStatus());
    }

    private void validateIsLeader(final Long loggedInMemberId, final Crew crew) {
        if (!crew.isLeader(loggedInMemberId)) {
            throw new CrewException(CREW_IS_NOT_LEADER, loggedInMemberId);
        }
    }

    @Transactional
    public void deleteCrewMember(final Long loggedInMemberId, final Long crewId, final Long memberId) {
        final CrewMember crewMember = findCrewMemberByCrewIdAndMemberId(crewId, memberId);
        final Crew crew = crewMember.getCrew();

        if (crew.isLeader(loggedInMemberId)) {
            validateIsLeaderSelfDeleted(loggedInMemberId, memberId);

            deleteCrewMember(crewMember);
            return;
        }

        if (loggedInMemberId.equals(memberId)) {
            cancelCrewMember(crewMember);
            return;
        }

        throw new CrewException(CREW_MEMBER_NOT_ALLOWED, loggedInMemberId);
    }

    private CrewMember findCrewMemberByCrewIdAndMemberId(final Long crewId, final Long memberId) {
        return crewMemberRepository.findByMemberIdAndCrewId(memberId, crewId)
                .orElseThrow(() -> new CrewException(CREW_MEMBER_NOT_FOUND, memberId, crewId));
    }

    private void validateIsLeaderSelfDeleted(Long loggedInMemberId, Long memberId) {
        if (loggedInMemberId.equals(memberId)) {
            throw new CrewException(CREW_LEADER_CANNOT_BE_DELETED, loggedInMemberId);
        }
    }

    private void cancelCrewMember(final CrewMember crewMember) {
        if (crewMember.getStatus() != WAITING) {
            throw new CrewException(CREW_MEMBER_STATUS_IS_NOT_WAITING);
        }

        deleteCrewMember(crewMember);
    }

    private void deleteCrewMember(final CrewMember crewMember) {
        crewMemberRepository.delete(crewMember);
    }
}
