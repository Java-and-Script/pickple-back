package kr.pickple.back.member.controller;

import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpStatus.*;

import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import kr.pickple.back.auth.config.property.JwtProperties;
import kr.pickple.back.member.dto.request.MemberCreateRequest;
import kr.pickple.back.member.dto.response.AuthenticatedMemberResponse;
import kr.pickple.back.member.dto.response.MemberProfileResponse;
import kr.pickple.back.member.service.MemberService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;
    private final JwtProperties jwtProperties;

    @PostMapping
    public ResponseEntity<AuthenticatedMemberResponse> createMember(
            @Valid @RequestBody MemberCreateRequest memberCreateRequest,
            final HttpServletResponse httpServletResponse
    ) {
        final AuthenticatedMemberResponse authenticatedMemberResponse = memberService.createMember(memberCreateRequest);
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

        return ResponseEntity.status(CREATED)
                .body(authenticatedMemberResponse);
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<MemberProfileResponse> findMemberProfileById(@PathVariable Long memberId) {
        return ResponseEntity.status(OK)
                .body(memberService.findMemberProfileById(memberId));
    }
}
