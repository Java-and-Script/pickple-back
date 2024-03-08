package kr.pickple.back.member.controller;

import static kr.pickple.back.member.exception.MemberExceptionCode.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpStatus.*;

import java.util.List;

import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import kr.pickple.back.auth.config.property.JwtProperties;
import kr.pickple.back.auth.config.resolver.Login;
import kr.pickple.back.auth.config.resolver.SignUp;
import kr.pickple.back.common.domain.RegistrationStatus;
import kr.pickple.back.crew.dto.response.CrewProfileResponse;
import kr.pickple.back.crew.service.CrewMemberService;
import kr.pickple.back.game.service.GameMemberService;
import kr.pickple.back.member.domain.NewMember;
import kr.pickple.back.member.dto.mapper.MemberRequestMapper;
import kr.pickple.back.member.dto.request.MemberCreateRequest;
import kr.pickple.back.member.dto.response.AuthenticatedMemberResponse;
import kr.pickple.back.crew.dto.response.CrewMemberRegistrationStatusResponse;
import kr.pickple.back.game.dto.response.GameMemberRegistrationStatusResponse;
import kr.pickple.back.game.dto.response.MemberGameResponse;
import kr.pickple.back.member.dto.response.MemberProfileResponse;
import kr.pickple.back.member.exception.MemberException;
import kr.pickple.back.member.service.MemberService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;
    private final CrewMemberService crewMemberService;
    private final GameMemberService gameMemberService;
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

        final NewMember newMember = MemberRequestMapper.mapToNewMemberDomain(memberCreateRequest);
        final AuthenticatedMemberResponse authenticatedMemberResponse = memberService.createMember(newMember);

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

    @Identification
    @GetMapping("/{memberId}/crews")
    public ResponseEntity<List<CrewProfileResponse>> findAllJoinedCrews(
            @Login final Long loggedInMemberId,
            @PathVariable final Long memberId,
            @RequestParam final RegistrationStatus status
    ) {
        return ResponseEntity.status(OK)
                .body(crewMemberService.findAllJoinedCrews(memberId, status));
    }

    @Identification
    @GetMapping("/{memberId}/created-crews")
    public ResponseEntity<List<CrewProfileResponse>> findCreatedCrews(
            @Login final Long loggedInMemberId,
            @PathVariable final Long memberId
    ) {
        return ResponseEntity.status(OK)
                .body(crewMemberService.findCreatedCrews(memberId));
    }

    @Identification
    @GetMapping("/{memberId}/crews/{crewId}/registration-status")
    public ResponseEntity<CrewMemberRegistrationStatusResponse> findRegistrationStatusForCrew(
            @Login final Long loggedInMemberId,
            @PathVariable final Long memberId,
            @PathVariable final Long crewId
    ) {
        return ResponseEntity.status(OK)
                .body(crewMemberService.findRegistrationStatusForCrew(memberId, crewId));
    }

    @Identification
    @GetMapping("/{memberId}/games")
    public ResponseEntity<List<MemberGameResponse>> findAllJoinedGames(
            @Login final Long loggedInMemberId,
            @PathVariable final Long memberId,
            @RequestParam final RegistrationStatus status
    ) {
        return ResponseEntity.status(OK)
                .body(gameMemberService.findAllJoinedGames(memberId, status));
    }

    @Identification
    @GetMapping("/{memberId}/created-games")
    public ResponseEntity<List<MemberGameResponse>> findAllCreatedGames(
            @Login final Long loggedInMemberId,
            @PathVariable final Long memberId
    ) {
        return ResponseEntity.status(OK)
                .body(gameMemberService.findAllCreatedGames(memberId));
    }

    @Identification
    @GetMapping("/{memberId}/games/{gameId}/registration-status")
    public ResponseEntity<GameMemberRegistrationStatusResponse> findRegistrationStatusForGame(
            @Login final Long loggedInMemberId,
            @PathVariable final Long memberId,
            @PathVariable final Long gameId
    ) {
        return ResponseEntity.status(OK)
                .body(gameMemberService.findRegistrationStatusForGame(memberId, gameId));
    }
}
