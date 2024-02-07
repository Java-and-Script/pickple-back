package kr.pickple.back.crew.dto.response;

import java.util.List;

import kr.pickple.back.address.dto.response.MainAddress;
import kr.pickple.back.crew.domain.Crew;
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

    public static CrewProfileResponse of(
            final Crew crew,
            final List<MemberResponse> memberResponses,
            final MainAddress mainAddress
    ) {
        return CrewProfileResponse.builder()
                .id(crew.getId())
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
                .addressDepth1(mainAddress.getAddressDepth1().getName())
                .addressDepth2(mainAddress.getAddressDepth2().getName())
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
