package kr.pickple.back.member.dto.response;

import java.util.List;

import kr.pickple.back.address.dto.response.MainAddress;
import kr.pickple.back.member.repository.entity.MemberEntity;
import kr.pickple.back.position.domain.Position;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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

    //TODO: member -> memberDomain 으로 변환 완료되면 삭제 예정 (김영주)
    public static MemberResponse of(
            final MemberEntity member,
            final List<Position> positions,
            final MainAddress mainAddress
    ) {
        return MemberResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .introduction(member.getIntroduction())
                .profileImageUrl(member.getProfileImageUrl())
                .mannerScore(member.getMannerScore())
                .mannerScoreCount(member.getMannerScoreCount())
                .addressDepth1(mainAddress.getAddressDepth1().getName())
                .addressDepth2(mainAddress.getAddressDepth2().getName())
                .positions(positions)
                .build();
    }
}
