package kr.pickple.back.crew.dto.response;

import kr.pickple.back.address.dto.response.MainAddress;
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

    public static CrewResponse of(final Crew crew, final MemberResponse leader, final MainAddress mainAddress) {
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
                .leader(leader)
                .addressDepth1(mainAddress.getAddressDepth1().getName())
                .addressDepth2(mainAddress.getAddressDepth2().getName())
                .build();
    }
}
