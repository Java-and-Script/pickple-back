package kr.pickple.back.crew.dto.response;

import java.util.List;

import kr.pickple.back.crew.domain.Crew;
import kr.pickple.back.crew.domain.CrewStatus;
import kr.pickple.back.member.dto.response.MemberResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CrewResponse {

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

    public static CrewResponse from(final Crew crew) {
        return CrewResponse.builder()
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
                .build();
    }

    public static List<CrewResponse> from(final List<Crew> crews) {
        return crews.stream()
                .map(CrewResponse::from)
                .toList();
    }
}
