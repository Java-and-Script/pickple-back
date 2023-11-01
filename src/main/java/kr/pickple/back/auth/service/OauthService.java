package kr.pickple.back.auth.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import kr.pickple.back.auth.domain.oauth.OauthMember;
import kr.pickple.back.auth.domain.oauth.OauthProvider;
import kr.pickple.back.auth.domain.token.AuthTokens;
import kr.pickple.back.auth.domain.token.JwtProvider;
import kr.pickple.back.auth.domain.token.RefreshToken;
import kr.pickple.back.auth.repository.RefreshTokenRepository;
import kr.pickple.back.auth.service.authcode.AuthCodeRequestUrlProviderComposite;
import kr.pickple.back.auth.service.memberclient.OauthMemberClientComposite;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.dto.response.AuthenticatedMemberResponse;
import kr.pickple.back.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OauthService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthCodeRequestUrlProviderComposite authCodeRequestUrlProviderComposite;
    private final OauthMemberClientComposite oauthMemberClientComposite;
    private final JwtProvider jwtProvider;

    public String getAuthCodeRequestUrl(final OauthProvider oauthProvider) {
        return authCodeRequestUrlProviderComposite.provide(oauthProvider);
    }

    public AuthenticatedMemberResponse processLoginOrRegistration(
            final OauthProvider oauthProvider,
            final String authCode
    ) {
        final OauthMember oauthMember = oauthMemberClientComposite.fetch(oauthProvider, authCode);
        final Optional<Member> member = memberRepository.findByOauthId(oauthMember.getOauthId());

        // 사용자가 로그인 하는 경우
        if (member.isPresent()) {
            final Member loginMember = member.get();
            final AuthTokens loginTokens = jwtProvider.createLoginToken(String.valueOf(loginMember.getId()));

            final RefreshToken refreshToken = RefreshToken.builder()
                    .token(loginTokens.getRefreshToken())
                    .memberId(loginMember.getId())
                    .build();

            refreshTokenRepository.save(refreshToken);

            return AuthenticatedMemberResponse.of(loginMember, loginTokens);
        }

        // 사용자가 회원 가입 시, 추가 정보(주 활동지역, 포지션)를 입력하는 경우
        final String oauthProviderName = oauthMember.getOauthProvider().name();
        final AuthTokens registerToken = jwtProvider.createRegisterToken(oauthProviderName + oauthMember.getOauthId());

        return AuthenticatedMemberResponse.of(oauthMember, registerToken);
    }
}
