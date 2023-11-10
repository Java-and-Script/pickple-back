package kr.pickple.back.member.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import kr.pickple.back.auth.config.property.JwtProperties;
import kr.pickple.back.auth.config.resolver.Login;
import kr.pickple.back.auth.config.resolver.SignUp;
import kr.pickple.back.common.domain.RegistrationStatus;
import kr.pickple.back.crew.dto.response.CrewProfileResponse;
import kr.pickple.back.game.dto.response.GameResponse;
import kr.pickple.back.member.dto.request.MemberCreateRequest;
import kr.pickple.back.member.dto.response.AuthenticatedMemberResponse;
import kr.pickple.back.member.dto.response.MemberProfileResponse;
import kr.pickple.back.member.exception.MemberException;
import kr.pickple.back.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static kr.pickple.back.member.exception.MemberExceptionCode.MEMBER_SIGNUP_OAUTH_SUBJECT_INVALID;
import static org.springframework.http.HttpHeaders.SET_COOKIE;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;
    private final JwtProperties jwtProperties;

    @PostMapping
    public ResponseEntity<AuthenticatedMemberResponse> createMember(
            @SignUp final String oauthSubject,
            @Valid @RequestBody final MemberCreateRequest memberCreateRequest,
            final HttpServletResponse httpServletResponse
    ) {
        final String oauthProviderName = memberCreateRequest.getOauthProvider().name();
        final String requestOauthSubject = oauthProviderName + memberCreateRequest.getOauthId();

        if (!oauthSubject.equals(requestOauthSubject)) {
            throw new MemberException(MEMBER_SIGNUP_OAUTH_SUBJECT_INVALID, requestOauthSubject);
        }

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
    public ResponseEntity<MemberProfileResponse> findMemberProfileById(@PathVariable final Long memberId) {
        return ResponseEntity.status(OK)
                .body(memberService.findMemberProfileById(memberId));
    }

    @GetMapping("/{memberId}/crews")
    public ResponseEntity<List<CrewProfileResponse>> findAllCrewsByMemberId(
            @Login final Long loggedInMemberId,
            @PathVariable final Long memberId,
            @RequestParam final RegistrationStatus status
    ) {
        return ResponseEntity.status(OK)
                .body(memberService.findAllCrewsByMemberId(memberId, status));
    }

    @GetMapping("/{memberId}/created-crews")
    public ResponseEntity<List<CrewProfileResponse>> findCreatedCrewsByMemberId(
            @Login final Long loggedInMemberId,
            @PathVariable final Long memberId
    ) {
        return ResponseEntity.status(OK)
                .body(memberService.findCreatedCrewsByMemberId(loggedInMemberId, memberId));
    }

    @GetMapping("/{memberId}/games")
    public ResponseEntity<List<GameResponse>> findAllMemberGames(
            @Login final Long loggedInMemberId,
            @PathVariable final Long memberId,
            @RequestParam final RegistrationStatus status
    ) {
        return ResponseEntity.status(OK)
                .body(memberService.findAllMemberGames(memberId, status));
    }

    @GetMapping("/{memberId}/created-games")
    public ResponseEntity<List<GameResponse>> findAllCreatedGames(
            @Login final Long loggedInMemberId,
            @PathVariable final Long memberId
    ) {
        return ResponseEntity.status(OK)
                .body(memberService.findAllCreatedGames(loggedInMemberId, memberId));
    }
}
