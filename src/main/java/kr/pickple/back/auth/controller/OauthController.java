package kr.pickple.back.auth.controller;

import java.io.IOException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import kr.pickple.back.auth.domain.oauth.OauthProvider;
import kr.pickple.back.auth.service.OauthService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class OauthController {

    private final OauthService oauthService;

    @GetMapping("/{oauthProvider}")
    public void redirectAuthCodeRequestUrl(
            @PathVariable final OauthProvider oauthProvider,
            final HttpServletResponse response
    ) throws IOException {
        final String redirectUrl = oauthService.getAuthCodeRequestUrl(oauthProvider);

        response.sendRedirect(redirectUrl);
    }

    @GetMapping("/login/{oauthProvider}")
    public void login(
            @PathVariable final OauthProvider oauthProvider,
            @RequestParam final String authCode
    ) {
        oauthService.processLoginOrRegistration(oauthProvider, authCode);
    }
}
