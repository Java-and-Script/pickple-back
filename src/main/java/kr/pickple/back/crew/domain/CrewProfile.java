package kr.pickple.back.crew.domain;

import java.util.List;

import kr.pickple.back.member.domain.Member;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CrewProfile {

    private Long crewId;
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
}
