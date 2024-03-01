package kr.pickple.back.crew.dto.mapper;

import java.util.List;

import kr.pickple.back.common.domain.RegistrationStatus;
import kr.pickple.back.crew.domain.Crew;
import kr.pickple.back.crew.domain.CrewProfile;
import kr.pickple.back.crew.dto.response.CrewIdResponse;
import kr.pickple.back.crew.dto.response.CrewProfileResponse;
import kr.pickple.back.crew.dto.response.CrewResponse;
import kr.pickple.back.member.domain.MemberDomain;
import kr.pickple.back.member.dto.mapper.MemberResponseMapper;
import kr.pickple.back.member.dto.response.CrewMemberRegistrationStatusResponse;
import kr.pickple.back.member.dto.response.MemberResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CrewResponseMapper {

    public static CrewIdResponse mapToCrewIdResponseDto(final Long crewId) {
        return CrewIdResponse.from(crewId);
    }

    public static CrewProfileResponse mapToCrewProfileResponseDto(final Crew crew, final List<MemberDomain> members) {
        final List<MemberResponse> memberResponses = members.stream()
                .map(MemberResponseMapper::mapToMemberResponseDto)
                .toList();

        return CrewProfileResponse.builder()
                .id(crew.getCrewId())
                .name(crew.getName())
                .content(crew.getContent())
                .memberCount(crew.getMemberCount())
                .maxMemberCount(crew.getMaxMemberCount())
                .profileImageUrl(crew.getProfileImageUrl())
                .backgroundImageUrl(crew.getBackgroundImageUrl())
                .status(crew.getStatus())
                .likeCount(crew.getLikeCount())
                .competitionPoint(crew.getCompetitionPoint())
                .leader(MemberResponseMapper.mapToMemberResponseDto(crew.getLeader()))
                .addressDepth1(crew.getAddressDepth1Name())
                .addressDepth2(crew.getAddressDepth2Name())
                .members(memberResponses)
                .build();
    }

    public static CrewResponse mapToCrewResponseDto(final Crew crew) {
        return CrewResponse.builder()
                .id(crew.getCrewId())
                .name(crew.getName())
                .content(crew.getContent())
                .memberCount(crew.getMemberCount())
                .maxMemberCount(crew.getMaxMemberCount())
                .profileImageUrl(crew.getProfileImageUrl())
                .backgroundImageUrl(crew.getBackgroundImageUrl())
                .status(crew.getStatus())
                .likeCount(crew.getLikeCount())
                .competitionPoint(crew.getCompetitionPoint())
                .leader(MemberResponseMapper.mapToMemberResponseDto(crew.getLeader()))
                .addressDepth1(crew.getAddressDepth1Name())
                .addressDepth2(crew.getAddressDepth2Name())
                .build();
    }

    public static List<CrewProfileResponse> mapToCrewProfilesResponseDto(
            final List<CrewProfile> crewProfiles
    ) {

        return crewProfiles.stream()
                .map(crewProfile -> CrewProfileResponse.builder()
                        .id(crewProfile.getCrewId())
                        .name(crewProfile.getName())
                        .content(crewProfile.getContent())
                        .memberCount(crewProfile.getMemberCount())
                        .maxMemberCount(crewProfile.getMaxMemberCount())
                        .profileImageUrl(crewProfile.getProfileImageUrl())
                        .backgroundImageUrl(crewProfile.getBackgroundImageUrl())
                        .status(crewProfile.getStatus())
                        .likeCount(crewProfile.getLikeCount())
                        .competitionPoint(crewProfile.getCompetitionPoint())
                        .leader(MemberResponseMapper.mapToMemberResponseDto(crewProfile.getLeader()))
                        .addressDepth1(crewProfile.getAddressDepth1())
                        .addressDepth2(crewProfile.getAddressDepth2())
                        .members(MemberResponseMapper.mapToMemberResponseDtos(crewProfile.getMembers()))
                        .build()
                )
                .toList();

    }

    public static CrewMemberRegistrationStatusResponse mapToCrewMemberRegistrationStatusResponse(
            final RegistrationStatus status
    ) {
        return CrewMemberRegistrationStatusResponse.from(status);
    }
}
