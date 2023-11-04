package kr.pickple.back.crew.dto.response;

import kr.pickple.back.crew.domain.Crew;
import kr.pickple.back.crew.domain.CrewStatus;
import kr.pickple.back.crew.dto.CrewMemberRelationDto;
import kr.pickple.back.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(staticName = "from")
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
    private Member leader;
    private String addressDepth1;
    private String addressDepth2;
    private List<CrewMemberRelationDto> crewMembers;

    public static CrewProfileResponse fromEntity(final Crew crew, final List<CrewMemberRelationDto> crewMemberList) {
        return CrewProfileResponse.from(
                crew.getId(),
                crew.getName(),
                crew.getContent(),
                crew.getMemberCount(),
                crew.getMaxMemberCount(),
                crew.getProfileImageUrl(),
                crew.getBackgroundImageUrl(),
                crew.getStatus(),
                crew.getLikeCount(),
                crew.getCompetitionPoint(),
                Member.builder().build(), //TODO: 추후 Member 도메인과 leader 연결 작업 추가(11월 1일, 소재훈)
                crew.getAddressDepth1().getName(),
                crew.getAddressDepth2().getName(),
                crewMemberList
        );
    }
}
