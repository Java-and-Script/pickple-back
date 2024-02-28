package kr.pickple.back.crew.dto.mapper;

import java.util.List;

import kr.pickple.back.crew.domain.Crew;
import kr.pickple.back.crew.dto.response.CrewIdResponse;
import kr.pickple.back.crew.dto.response.CrewProfileResponse;
import kr.pickple.back.crew.dto.response.CrewResponse;
import kr.pickple.back.member.domain.MemberDomain;
import kr.pickple.back.member.dto.mapper.MemberResponseMapper;
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
}
