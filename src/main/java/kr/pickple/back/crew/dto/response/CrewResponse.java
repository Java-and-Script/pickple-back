package kr.pickple.back.crew.dto.response;

import kr.pickple.back.crew.domain.CrewStatus;
import kr.pickple.back.member.dto.response.MemberResponse;
import lombok.Getter;

@Getter
public class CrewResponse {

    private Long id;
    private String name;
    private String content;
    private Integer memberCount;
    private String profileImageUrl;
    private String backgroundImageUrl;
    private CrewStatus status;
    private Integer likeCount;
    private Integer maxMemberCount;
    private Integer competitionPoint;
    private MemberResponse leader;
    private String addressDepth1;
    private String addressDepth2;
}
