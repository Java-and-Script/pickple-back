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
import kr.pickple.back.member.domain.MemberProfile;
import kr.pickple.back.member.domain.NewMember;
import kr.pickple.back.member.dto.mapper.MemberRequestMapper;
import kr.pickple.back.member.dto.mapper.MemberResponseMapper;
import kr.pickple.back.member.dto.request.MemberCreateRequest;
import kr.pickple.back.member.dto.response.AuthenticatedMemberResponse;
import kr.pickple.back.member.dto.response.CrewMemberRegistrationStatusResponse;
import kr.pickple.back.member.dto.response.GameMemberRegistrationStatusResponse;
import kr.pickple.back.member.dto.response.MemberGameResponse;
import kr.pickple.back.member.dto.response.MemberProfileResponse;
import kr.pickple.back.member.exception.MemberException;
import kr.pickple.back.member.service.MemberCrewService;
import kr.pickple.back.member.service.MemberGameService;
import kr.pickple.back.member.service.MemberService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;
    private final MemberCrewService memberCrewService;
    private final MemberGameService memberGameService;
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
        final NewMember savedNewMember = memberService.createMember(newMember);
        final AuthenticatedMemberResponse authenticatedMemberResponse = MemberResponseMapper
                .mapToAuthenticatedMemberResponseDto(savedNewMember);

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
        final MemberProfile memberProfile = memberService.findMemberProfileById(memberId);
        final MemberProfileResponse memberProfileResponse = MemberResponseMapper
                .mapToMemberProfileResponseDto(memberProfile);

        return ResponseEntity.status(OK)
                .body(memberProfileResponse);
    }

    @GetMapping("/{memberId}/crews")
    public ResponseEntity<List<CrewProfileResponse>> findAllCrewsByMemberId(
            @Login final Long loggedInMemberId,
            @PathVariable final Long memberId,
            @RequestParam final RegistrationStatus status
    ) {
        return ResponseEntity.status(OK)
                .body(memberCrewService.findAllCrewsByMemberId(loggedInMemberId, memberId, status));
    }

    @GetMapping("/{memberId}/created-crews")
    public ResponseEntity<List<CrewProfileResponse>> findCreatedCrewsByMemberId(
            @Login final Long loggedInMemberId,
            @PathVariable final Long memberId
    ) {
        return ResponseEntity.status(OK)
                .body(memberCrewService.findCreatedCrewsByMemberId(loggedInMemberId, memberId));
    }

    @GetMapping("/{memberId}/games")
    public ResponseEntity<List<MemberGameResponse>> findAllMemberGames(
            @Login final Long loggedInMemberId,
            @PathVariable final Long memberId,
            @RequestParam final RegistrationStatus status
    ) {
        return ResponseEntity.status(OK)
                .body(memberGameService.findAllMemberGames(loggedInMemberId, memberId, status));
    }

    @GetMapping("/{memberId}/created-games")
    public ResponseEntity<List<MemberGameResponse>> findAllCreatedGames(
            @Login final Long loggedInMemberId,
            @PathVariable final Long memberId
    ) {
        return ResponseEntity.status(OK)
                .body(memberGameService.findAllCreatedGames(loggedInMemberId, memberId));
    }

    @GetMapping("/{memberId}/games/{gameId}/registration-status")
    public ResponseEntity<GameMemberRegistrationStatusResponse> findMemberRegistrationStatusForGame(
            @Login final Long loggedInMemberId,
            @PathVariable final Long memberId,
            @PathVariable final Long gameId
    ) {
        return ResponseEntity.status(OK)
                .body(memberGameService.findMemberRegistrationStatusForGame(loggedInMemberId, memberId, gameId));
    }

    @GetMapping("/{memberId}/crews/{crewId}/registration-status")
    public ResponseEntity<CrewMemberRegistrationStatusResponse> findMemberRegistrationStatusForCrew(
            @Login final Long loggedInMemberId,
            @PathVariable final Long memberId,
            @PathVariable final Long crewId
    ) {
        return ResponseEntity.status(OK)
                .body(memberCrewService.findMemberRegistrationStatusForCrew(loggedInMemberId, memberId, crewId));
    }
}
