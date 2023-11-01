package kr.pickple.back.member.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import kr.pickple.back.address.dto.response.MainAddressResponse;
import kr.pickple.back.auth.domain.oauth.OauthProvider;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.domain.MemberStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberCreateRequest {

    @NotBlank(message = "닉네임은 null이거나 빈 문자열이거나 공백일 수 없음")
    private String nickname;

    @NotBlank(message = "프로필 이미지 URL은 null이거나 빈 문자열이거나 공백일 수 없음")
    private String profileImageUrl;

    @NotBlank(message = "이메일은 null이거나 빈 문자열이거나 공백일 수 없음")
    private String email;

    @NotNull(message = "포지션은 null일 수 없음")
    private List<String> positions;

    @NotNull(message = "oauth id는 null일 수 없음")
    @Positive(message = "oauth id는 값이 없거나 음수일 수 없음")
    private Long oauthId;

    @NotNull(message = "oauth 제공자는 null이거나 빈 문자열이거나 공백일 수 없음")
    private OauthProvider oauthProvider;

    @NotBlank(message = "주소1은 null이거나 빈 문자열이거나 공백일 수 없음")
    private String addressDepth1;

    @NotBlank(message = "주소2는 null이거나 빈 문자열이거나 공백일 수 없음")
    private String addressDepth2;

    public Member toEntity(final MainAddressResponse mainAddressResponse) {
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
