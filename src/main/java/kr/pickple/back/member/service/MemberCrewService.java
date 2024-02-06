package kr.pickple.back.member.service;

import static kr.pickple.back.common.domain.RegistrationStatus.*;
import static kr.pickple.back.member.exception.MemberExceptionCode.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.address.implement.AddressReader;
import kr.pickple.back.common.domain.RegistrationStatus;
import kr.pickple.back.crew.domain.Crew;
import kr.pickple.back.crew.domain.CrewMember;
import kr.pickple.back.crew.dto.response.CrewProfileResponse;
import kr.pickple.back.crew.repository.CrewMemberRepository;
import kr.pickple.back.crew.repository.CrewRepository;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.domain.MemberPosition;
import kr.pickple.back.member.dto.response.CrewMemberRegistrationStatusResponse;
import kr.pickple.back.member.dto.response.MemberResponse;
import kr.pickple.back.member.exception.MemberException;
import kr.pickple.back.member.repository.MemberPositionRepository;
import kr.pickple.back.member.repository.MemberRepository;
import kr.pickple.back.position.domain.Position;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberCrewService {

    private final MemberRepository memberRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final CrewRepository crewRepository;
    private final MemberPositionRepository memberPositionRepository;
    private final AddressReader addressReader;

    /**
     * 사용자가 가입한 크루 목록 조회
     */
    public List<CrewProfileResponse> findAllCrewsByMemberId(
            final Long loggedInMemberId,
            final Long memberId,
            final RegistrationStatus memberStatus
    ) {
        validateSelfMemberAccess(loggedInMemberId, memberId);

        final Member member = memberRepository.getMemberById(memberId);
        final List<Crew> crews = crewMemberRepository.findAllByMemberIdAndStatus(member.getId(), memberStatus)
                .stream()
                .map(CrewMember::getCrew)
                .toList();

        return convertToCrewProfileResponses(crews, memberStatus);
    }

    /**
     * 사용자가 만든 크루 목록 조회
     */
    public List<CrewProfileResponse> findCreatedCrewsByMemberId(final Long loggedInMemberId, final Long memberId) {
        validateSelfMemberAccess(loggedInMemberId, memberId);

        final Member member = memberRepository.getMemberById(memberId);
        final List<Crew> crews = crewRepository.findAllByLeaderId(member.getId());

        return convertToCrewProfileResponses(crews, CONFIRMED);
    }

    /**
     * 회원의 크루 가입 신청 여부 조회
     */
    public CrewMemberRegistrationStatusResponse findMemberRegistrationStatusForCrew(
            final Long loggedInMemberId,
            final Long memberId,
            final Long crewId
    ) {
        validateSelfMemberAccess(loggedInMemberId, memberId);

        final CrewMember crewMember = crewMemberRepository.getCrewMemberByCrewIdAndMemberId(crewId, memberId);

        return CrewMemberRegistrationStatusResponse.from(crewMember.getStatus());
    }

    private List<CrewProfileResponse> convertToCrewProfileResponses(
            final List<Crew> crews,
            final RegistrationStatus memberStatus
    ) {

        return crews.stream()
                .map(crew -> CrewProfileResponse.of(crew, getMemberResponsesByCrew(crew, memberStatus)))
                .toList();
    }

    private List<MemberResponse> getMemberResponsesByCrew(final Crew crew, final RegistrationStatus memberStatus) {
        return crewMemberRepository.findAllByCrewIdAndStatus(crew.getId(), memberStatus)
                .stream()
                .map(CrewMember::getMember)
                .map(member -> MemberResponse.of(member, getPositions(member), addressReader.readMainAddress(member)))
                .toList();
    }

    private List<Position> getPositions(final Member member) {
        final List<MemberPosition> memberPositions = memberPositionRepository.findAllByMemberId(
                member.getId());

        return Position.fromMemberPositions(memberPositions);
    }

    private void validateSelfMemberAccess(final Long loggedInMemberId, final Long memberId) {
        if (!loggedInMemberId.equals(memberId)) {
            throw new MemberException(MEMBER_MISMATCH, loggedInMemberId, memberId);
        }
    }
}
