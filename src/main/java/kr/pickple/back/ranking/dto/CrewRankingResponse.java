package kr.pickple.back.ranking.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CrewRankingResponse {

    private Long id;
    private String name;
    private Integer memberCount;
    private Integer maxMemberCount;
    private String profileImageUrl;
    private String addressDepth1;
    private String addressDepth2;
    private Integer mannerScore;
    private Integer totalScore;
    private Integer rank;
}
