package kr.pickple.back.auth.controller;

import jakarta.servlet.http.HttpServletResponse;
import kr.pickple.back.auth.config.property.JwtProperties;
import kr.pickple.back.auth.config.resolver.Login;
import kr.pickple.back.auth.domain.oauth.OauthProvider;
import kr.pickple.back.auth.dto.response.AccessTokenResponse;
import kr.pickple.back.auth.service.OauthService;
import kr.pickple.back.member.dto.response.AuthenticatedMemberResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static org.springframework.http.HttpHeaders.SET_COOKIE;
import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class OauthController {

    private static final String COOKIE_TOKEN = "refresh-token";
    private static final Integer COOKIE_LOGOUT_MAX_AGE = 0;

    private final OauthService oauthService;
    private final JwtProperties jwtProperties;

    @GetMapping("/{oauthProvider}")
    public void redirectAuthCodeRequestUrl(
            @PathVariable final OauthProvider oauthProvider,
            final HttpServletResponse response
    ) throws IOException {
        final String redirectUrl = oauthService.getAuthCodeRequestUrl(oauthProvider);

        response.sendRedirect(redirectUrl);
    }

    @GetMapping("/login/{oauthProvider}")
    public ResponseEntity<AuthenticatedMemberResponse> login(
            @PathVariable final OauthProvider oauthProvider,
            @RequestParam final String authCode,
            final HttpServletResponse httpServletResponse
    ) {
        final AuthenticatedMemberResponse authenticatedMemberResponse = oauthService.processLoginOrRegistration(
                oauthProvider, authCode);
        final String refreshToken = authenticatedMemberResponse.getRefreshToken();

        if (refreshToken != null) {
            final ResponseCookie cookie = ResponseCookie.from(COOKIE_TOKEN, refreshToken)
                    .maxAge(jwtProperties.getRefreshTokenExpirationTime())
                    .sameSite("None")
                    .secure(true)
                    .httpOnly(true)
                    .path("/")
                    .build();

            httpServletResponse.addHeader(SET_COOKIE, cookie.toString());
        }

        return ResponseEntity.status(OK)
                .body(authenticatedMemberResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AccessTokenResponse> regenerateAccessToken(
            @CookieValue(COOKIE_TOKEN) final String refreshToken,
            @RequestHeader("Authorization") final String authorizationHeader
    ) {
        final AccessTokenResponse regeneratedAccessToken = oauthService.regenerateAccessToken(refreshToken,
                authorizationHeader);

        return ResponseEntity.status(CREATED)
                .body(regeneratedAccessToken);
    }

    @DeleteMapping("/logout")
    public ResponseEntity<Void> logout(
            @Login final Long loggedInMemberId,
            @CookieValue(COOKIE_TOKEN) final String refreshToken,
            final HttpServletResponse httpServletResponse
    ) {
        oauthService.deleteRefreshToken(refreshToken);
        final ResponseCookie cookie = ResponseCookie.from(COOKIE_TOKEN, "")
                .maxAge(COOKIE_LOGOUT_MAX_AGE)
                .sameSite("None")
                .secure(true)
                .httpOnly(true)
                .path("/")
                .build();

        httpServletResponse.addHeader(SET_COOKIE, cookie.toString());
        return ResponseEntity.status(NO_CONTENT)
                .build();
    }
}
