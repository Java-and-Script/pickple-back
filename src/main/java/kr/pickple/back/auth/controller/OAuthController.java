package kr.pickple.back.auth.controller;

import java.io.IOException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import kr.pickple.back.auth.domain.oauth.OAuthProvider;
import kr.pickple.back.auth.service.OAuthService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class OAuthController {

    private final OAuthService oAuthService;

    @GetMapping("/{oAuthProvider}")
    public void redirectAuthCodeRequestUrl(
            @PathVariable final OAuthProvider oAuthProvider,
            final HttpServletResponse response
    ) throws IOException {
        final String redirectUrl = oAuthService.getAuthCodeRequestUrl(oAuthProvider);

        response.sendRedirect(redirectUrl);
    }

    @GetMapping("/login/{oAuthProvider}")
    public void login(
            @PathVariable final OAuthProvider oAuthProvider,
            @RequestParam("authCode") final String authCode
    ) {
        oAuthService.processLoginOrRegistration(oAuthProvider, authCode);
    }
}
