package kr.pickple.back.member.dto.response;

import java.util.List;

import kr.pickple.back.position.domain.Position;
import lombok.Getter;

@Getter
public class MemberResponse {

    private Long id;
    private String email;
    private String nickname;
    private String introduction;
    private String profileImageUrl;
    private Integer mannerScore;
    private Integer mannerScoreCount;
    private String addressDepth1;
    private String addressDepth2;
    private List<Position> positions;
}
