package kr.pickple.back.crew.dto.response;

import kr.pickple.back.crew.domain.Crew;
import kr.pickple.back.crew.domain.CrewStatus;
import kr.pickple.back.member.domain.Member;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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
    private List<Member> members;

    public static CrewProfileResponse fromEntity(final Crew crew) {
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
                .leader(Member.builder().build())
                .members(Arrays.asList(Member.builder().build()))
                .addressDepth1(crew.getAddressDepth1().getName())
                .addressDepth2(crew.getAddressDepth2().getName())
                //TODO:추후 Member 도메인 완성되면 추가(11월 1일, 소재훈)
                .build();
    }
}
