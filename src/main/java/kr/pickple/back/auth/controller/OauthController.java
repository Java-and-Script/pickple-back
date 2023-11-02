package kr.pickple.back.auth.controller;

import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpStatus.*;

import java.io.IOException;

import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import kr.pickple.back.auth.config.property.JwtProperties;
import kr.pickple.back.auth.domain.oauth.OauthProvider;
import kr.pickple.back.auth.dto.response.AccessTokenResponse;
import kr.pickple.back.auth.service.OauthService;
import kr.pickple.back.member.dto.response.AuthenticatedMemberResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class OauthController {

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
            final ResponseCookie cookie = ResponseCookie.from("refresh-token", refreshToken)
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
            @CookieValue("refresh-token") final String refreshToken,
            @RequestHeader("Authorization") final String authorizationHeader
    ) {
        final AccessTokenResponse modifiedAccessToken = oauthService.regenerateAccessToken(refreshToken,
                authorizationHeader);

        return ResponseEntity.status(CREATED)
                .body(modifiedAccessToken);
    }
}
