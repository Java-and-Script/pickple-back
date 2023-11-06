package kr.pickple.back.crew.dto.response;

import kr.pickple.back.crew.domain.Crew;
import kr.pickple.back.crew.domain.CrewStatus;
import kr.pickple.back.member.dto.response.MemberResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

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

    public static CrewProfileResponse fromEntity(final Crew crew, final List<MemberResponse> crewMemberList) {
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
                .leader(MemberResponse.from(crew.getLeader()))
                .addressDepth1(crew.getAddressDepth1().getName())
                .addressDepth2(crew.getAddressDepth2().getName())
                .members(crewMemberList)
                .build();
    }
}
