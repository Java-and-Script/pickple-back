package kr.pickple.back.member.dto;

import java.util.List;

import kr.pickple.back.address.dto.response.MainAddressResponse;
import kr.pickple.back.auth.domain.oauth.OAuthProvider;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.domain.MemberStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberCreateRequest {

    private String nickname;
    private String profileImageUrl;
    private String email;
    private List<String> positions;
    private Long oauthId;
    private OAuthProvider oauthProvider;
    private String addressDepth1;
    private String addressDepth2;

    public Member toEntity(MainAddressResponse mainAddressResponse) {
        return Member.builder()
                .email(email)
                .nickname(nickname)
                .profileImageUrl(profileImageUrl)
                .status(MemberStatus.ACTIVE)
                .oauthId(oauthId)
                .oauthProvider(oauthProvider)
                .addressDepth1(mainAddressResponse.getAddressDepth1())
                .addressDepth2(mainAddressResponse.getAddressDepth2())
                .build();
    }
}
