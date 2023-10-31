package kr.pickple.back.member.dto.response;

import java.util.List;

import kr.pickple.back.crew.dto.response.CrewResponse;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.position.domain.Position;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberProfileResponse {

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
    private List<CrewResponse> crews; //TODO: 추후 Crew 도메인 완성 시, 해당 필드에 대한 로직 추가 예정 (10.31 김영주)

    public static MemberProfileResponse of(final Member member, final List<Position> positions) {
        return MemberProfileResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .introduction(member.getIntroduction())
                .profileImageUrl(member.getProfileImageUrl())
                .mannerScore(member.getMannerScore())
                .mannerScoreCount(member.getMannerScoreCount())
                .addressDepth1(member.getAddressDepth1().getName())
                .addressDepth2(member.getAddressDepth2().getName())
                .positions(positions)
                .build();
    }
}
