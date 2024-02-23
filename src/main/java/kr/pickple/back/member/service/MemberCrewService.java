package kr.pickple.back.member.service;

import static kr.pickple.back.common.domain.RegistrationStatus.*;
import static kr.pickple.back.member.exception.MemberExceptionCode.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.address.implement.AddressReader;
import kr.pickple.back.common.domain.RegistrationStatus;
import kr.pickple.back.crew.repository.entity.CrewEntity;
import kr.pickple.back.crew.repository.entity.CrewMemberEntity;
import kr.pickple.back.crew.dto.response.CrewProfileResponse;
import kr.pickple.back.crew.repository.CrewMemberRepository;
import kr.pickple.back.crew.repository.CrewRepository;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.domain.MemberPosition;
import kr.pickple.back.member.dto.response.CrewMemberRegistrationStatusResponse;
import kr.pickple.back.member.dto.response.MemberResponse;
import kr.pickple.back.member.exception.MemberException;
import kr.pickple.back.member.implement.MemberReader;
import kr.pickple.back.position.domain.Position;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberCrewService {

    private final AddressReader addressReader;
    private final MemberReader memberReader;
    private final CrewMemberRepository crewMemberRepository;
    private final CrewRepository crewRepository;

    /**
     * 사용자가 가입한 크루 목록 조회
     */
    public List<CrewProfileResponse> findAllCrewsByMemberId(
            final Long loggedInMemberId,
            final Long memberId,
            final RegistrationStatus memberStatus
    ) {
        validateSelfMemberAccess(loggedInMemberId, memberId);

        final Member member = memberReader.readEntityByMemberId(memberId);
        final List<CrewEntity> crews = crewMemberRepository.findAllByMemberIdAndStatus(member.getId(), memberStatus)
                .stream()
                .map(crewMember -> crewRepository.getCrewById(crewMember.getCrewId()))
                .toList();

        return convertToCrewProfileResponses(crews, memberStatus);
    }

    /**
     * 사용자가 만든 크루 목록 조회
     */
    public List<CrewProfileResponse> findCreatedCrewsByMemberId(final Long loggedInMemberId, final Long memberId) {
        validateSelfMemberAccess(loggedInMemberId, memberId);

        final Member member = memberReader.readEntityByMemberId(memberId);
        final List<CrewEntity> crews = crewRepository.findAllByLeaderId(member.getId());

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

        final CrewMemberEntity crewMember = crewMemberRepository.getCrewMemberByCrewIdAndMemberId(crewId, memberId);

        return CrewMemberRegistrationStatusResponse.from(crewMember.getStatus());
    }

    private List<CrewProfileResponse> convertToCrewProfileResponses(
            final List<CrewEntity> crews,
            final RegistrationStatus memberStatus
    ) {

        return crews.stream()
                .map(crew -> CrewProfileResponse.of(
                                crew,
                                getMemberResponsesByCrew(crew, memberStatus),
                                addressReader.readMainAddressById(crew.getAddressDepth1Id(), crew.getAddressDepth2Id())
                        )
                )
                .toList();
    }

    private List<MemberResponse> getMemberResponsesByCrew(final CrewEntity crew, final RegistrationStatus memberStatus) {
        return crewMemberRepository.findAllByCrewIdAndStatus(crew.getId(), memberStatus)
                .stream()
                .map(crewMember -> memberReader.readEntityByMemberId(crewMember.getMemberId()))
                .map(member -> MemberResponse.of(
                                member,
                                getPositions(member),
                                addressReader.readMainAddressById(member.getAddressDepth1Id(), member.getAddressDepth2Id()
                                )
                        )
                )
                .toList();
    }

    private List<Position> getPositions(final Member member) {
        final List<MemberPosition> memberPositions = memberPositionReader.readAll(member.getId());

        return Position.fromMemberPositions(memberPositions);
    }

    private void validateSelfMemberAccess(final Long loggedInMemberId, final Long memberId) {
        if (!loggedInMemberId.equals(memberId)) {
            throw new MemberException(MEMBER_MISMATCH, loggedInMemberId, memberId);
        }
    }
}
