package kr.pickple.back.member.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.common.domain.RegistrationStatus;
import kr.pickple.back.crew.domain.CrewMember;
import kr.pickple.back.crew.domain.CrewProfile;
import kr.pickple.back.crew.dto.mapper.CrewResponseMapper;
import kr.pickple.back.crew.dto.response.CrewProfileResponse;
import kr.pickple.back.crew.implement.CrewReader;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.dto.response.CrewMemberRegistrationStatusResponse;
import kr.pickple.back.member.implement.MemberReader;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberCrewService {

    private final MemberReader memberReader;
    private final CrewReader crewReader;

    /**
     * 사용자가 가입한 크루 목록 조회
     */
    public List<CrewProfileResponse> findAllCrewsByMemberId(
            final Long memberId,
            final RegistrationStatus memberStatus
    ) {
        final Member member = memberReader.readByMemberId(memberId);
        final List<CrewProfile> crewProfiles = crewReader.readAllCrewProfilesByMemberIdAndStatus(
                member.getMemberId(),
                memberStatus
        );

        return CrewResponseMapper.mapToCrewProfilesResponseDto(crewProfiles);
    }

    /**
     * 사용자가 만든 크루 목록 조회
     */
    public List<CrewProfileResponse> findCreatedCrewsByMemberId(final Long memberId) {
        final Member member = memberReader.readByMemberId(memberId);
        final List<CrewProfile> crewProfiles = crewReader.readAllCrewProfilesByLeaderId(member.getMemberId());

        return CrewResponseMapper.mapToCrewProfilesResponseDto(crewProfiles);
    }

    /**
     * 회원의 크루 가입 신청 여부 조회
     */
    public CrewMemberRegistrationStatusResponse findMemberRegistrationStatusForCrew(
            final Long memberId,
            final Long crewId
    ) {
        final CrewMember crewMember = crewReader.readCrewMember(crewId, memberId);

        return CrewResponseMapper.mapToCrewMemberRegistrationStatusResponse(crewMember.getStatus());
    }
}
