package kr.pickple.back.fixture.dto;

import java.util.List;

import kr.pickple.back.auth.domain.oauth.OauthProvider;
import kr.pickple.back.member.dto.request.MemberCreateRequest;

public class MemberDtoFixtures {

    public static MemberCreateRequest memberCreateRequestBuild() {
        return MemberCreateRequest.builder()
                .nickname("강백둥")
                .profileImageUrl("https://amazon.image/1")
                .email("pickple1@pickple.kr")
                .positions(List.of("SG", "C"))
                .oauthId(1L)
                .oauthProvider(OauthProvider.KAKAO)
                .addressDepth1("서울시")
                .addressDepth2("영등포구")
                .build();
    }
}
