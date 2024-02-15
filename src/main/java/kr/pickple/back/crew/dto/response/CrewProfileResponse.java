package kr.pickple.back.crew.dto.response;

import java.util.List;

import kr.pickple.back.crew.domain.CrewDomain;
import kr.pickple.back.crew.domain.CrewStatus;
import kr.pickple.back.member.dto.response.MemberResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CrewProfileResponse {

    private Long id;
    private String name;
    private String content;
    private Integer memberCount;
    private Integer maxMemberCount;
    private String profileImageUrl;
    private String backgroundImageUrl;
    private CrewStatus status;
    private Integer likeCount;
    private Integer competitionPoint;
    private MemberResponse leader;
    private String addressDepth1;
    private String addressDepth2;
    private List<MemberResponse> members;

    public static CrewProfileResponse of(final CrewDomain crew, final List<MemberResponse> memberResponses) {
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
                .leader(getLeaderResponse(memberResponses, crew.getLeader().getId()))
                .addressDepth1(crew.getAddressDepth1Name())
                .addressDepth2(crew.getAddressDepth2Name())
                .members(memberResponses)
                .build();
    }

    private static MemberResponse getLeaderResponse(final List<MemberResponse> memberResponses, final Long leaderId) {
        return memberResponses.stream()
                .filter(memberResponse -> memberResponse.getId().equals(leaderId))
                .findFirst()
                .orElseThrow();
    }
}
