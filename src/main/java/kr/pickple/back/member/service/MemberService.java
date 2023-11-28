package kr.pickple.back.member.service;

import static kr.pickple.back.common.domain.RegistrationStatus.*;
import static kr.pickple.back.crew.exception.CrewExceptionCode.*;
import static kr.pickple.back.game.exception.GameExceptionCode.*;
import static kr.pickple.back.member.exception.MemberExceptionCode.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.address.dto.response.MainAddressResponse;
import kr.pickple.back.address.service.AddressService;
import kr.pickple.back.auth.config.property.JwtProperties;
import kr.pickple.back.auth.domain.token.AuthTokens;
import kr.pickple.back.auth.domain.token.JwtProvider;
import kr.pickple.back.auth.domain.token.RefreshToken;
import kr.pickple.back.auth.repository.RedisRepository;
import kr.pickple.back.common.domain.RegistrationStatus;
import kr.pickple.back.crew.domain.Crew;
import kr.pickple.back.crew.dto.response.CrewProfileResponse;
import kr.pickple.back.crew.dto.response.CrewResponse;
import kr.pickple.back.crew.exception.CrewException;
import kr.pickple.back.crew.repository.CrewRepository;
import kr.pickple.back.game.domain.Game;
import kr.pickple.back.game.dto.response.GameResponse;
import kr.pickple.back.game.exception.GameException;
import kr.pickple.back.game.repository.GameRepository;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.dto.request.MemberCreateRequest;
import kr.pickple.back.member.dto.response.AuthenticatedMemberResponse;
import kr.pickple.back.member.dto.response.MemberProfileResponse;
import kr.pickple.back.member.dto.response.MemberResponse;
import kr.pickple.back.member.dto.response.RegistrationStatusResponse;
import kr.pickple.back.member.exception.MemberException;
import kr.pickple.back.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private static final String REFRESH_TOKEN_KEY = "refresh_token";

    private final AddressService addressService;
    private final MemberRepository memberRepository;
    private final CrewRepository crewRepository;
    private final GameRepository gameRepository;
    private final RedisRepository redisRepository;
    private final JwtProvider jwtProvider;
    private final JwtProperties jwtProperties;

    @Transactional
    public AuthenticatedMemberResponse createMember(final MemberCreateRequest memberCreateRequest) {
        validateIsDuplicatedMemberInfo(memberCreateRequest);

        final MainAddressResponse mainAddressResponse = addressService.findMainAddressByNames(
                memberCreateRequest.getAddressDepth1(),
                memberCreateRequest.getAddressDepth2()
        );

        final Member member = memberCreateRequest.toEntity(mainAddressResponse);
        final Member savedMember = memberRepository.save(member);

        final AuthTokens loginTokens = jwtProvider.createLoginToken(String.valueOf(savedMember.getId()));

        final RefreshToken refreshToken = RefreshToken.builder()
                .token(loginTokens.getRefreshToken())
                .memberId(savedMember.getId())
                .createdAt(LocalDateTime.now())
                .build();

        redisRepository.saveHash(
                REFRESH_TOKEN_KEY,
                refreshToken.getToken(),
                refreshToken,
                jwtProperties.getRefreshTokenExpirationTime()
        );

        return AuthenticatedMemberResponse.of(savedMember, loginTokens);
    }

    private void validateIsDuplicatedMemberInfo(final MemberCreateRequest memberCreateRequest) {
        final String email = memberCreateRequest.getEmail();
        final String nickname = memberCreateRequest.getNickname();
        final Long oauthId = memberCreateRequest.getOauthId();

        if (memberRepository.existsByEmailOrNicknameOrOauthId(email, nickname, oauthId)) {
            throw new MemberException(MEMBER_IS_EXISTED, email, nickname, oauthId);
        }
    }

    public MemberProfileResponse findMemberProfileById(final Long memberId) {
        final Member member = findMemberById(memberId);
        final List<CrewResponse> crewResponses = member.getCrewsByStatus(CONFIRMED)
                .stream()
                .map(CrewResponse::from)
                .toList();

        return MemberProfileResponse.of(member, crewResponses);
    }

    public List<CrewProfileResponse> findAllCrewsByMemberId(
            final Long loggedInMemberId,
            final Long memberId,
            final RegistrationStatus memberStatus
    ) {
        validateSelfMemberAccess(loggedInMemberId, memberId);

        final Member member = findMemberById(memberId);
        final List<Crew> crews = member.getCrewsByStatus(memberStatus);

        return convertToCrewProfileResponses(crews, memberStatus);
    }

    public List<CrewProfileResponse> findCreatedCrewsByMemberId(final Long loggedInMemberId, final Long memberId) {
        validateSelfMemberAccess(loggedInMemberId, memberId);

        final Member member = findMemberById(memberId);
        final List<Crew> crews = member.getCreatedCrews();

        return convertToCrewProfileResponses(crews, CONFIRMED);
    }

    private List<CrewProfileResponse> convertToCrewProfileResponses(
            final List<Crew> crews,
            final RegistrationStatus memberStatus
    ) {
        return crews.stream()
                .map(crew -> CrewProfileResponse.of(crew, getMemberResponsesByCrew(crew, memberStatus)))
                .toList();
    }

    private List<MemberResponse> getMemberResponsesByCrew(final Crew crew, final RegistrationStatus memberStatus) {
        return crew.getMembersByStatus(memberStatus)
                .stream()
                .map(MemberResponse::from)
                .toList();
    }

    public List<GameResponse> findAllMemberGames(
            final Long loggedInMemberId,
            final Long memberId,
            final RegistrationStatus memberStatus
    ) {
        validateSelfMemberAccess(loggedInMemberId, memberId);

        final Member member = findMemberById(memberId);
        final List<Game> games = member.getGamesByStatus(memberStatus);

        return convertToGameResponses(games, memberStatus);
    }

    public List<GameResponse> findAllCreatedGames(final Long loggedInMemberId, final Long memberId) {
        validateSelfMemberAccess(loggedInMemberId, memberId);

        final Member member = findMemberById(memberId);
        final List<Game> games = member.getCreatedGames();

        return convertToGameResponses(games, CONFIRMED);
    }

    private void validateSelfMemberAccess(Long loggedInMemberId, Long memberId) {
        if (!loggedInMemberId.equals(memberId)) {
            throw new MemberException(MEMBER_MISMATCH, loggedInMemberId, memberId);
        }
    }

    private Member findMemberById(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND, memberId));
    }

    private Crew findCrewById(final Long crewId) {
        return crewRepository.findById(crewId)
                .orElseThrow(() -> new CrewException(CREW_NOT_FOUND, crewId));
    }

    private Game findGameById(final Long gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new GameException(GAME_NOT_FOUND, gameId));
    }

    private List<GameResponse> convertToGameResponses(final List<Game> games, final RegistrationStatus memberStatus) {
        return games.stream()
                .map(game -> GameResponse.of(game, getMemberResponsesByGame(game, memberStatus)))
                .toList();
    }

    private List<MemberResponse> getMemberResponsesByGame(final Game game, final RegistrationStatus memberStatus) {
        return game.getMembersByStatus(memberStatus)
                .stream()
                .map(MemberResponse::from)
                .toList();
    }

    public RegistrationStatusResponse findMemberRegistationStatusForGame(
            final Long loggedInMemberId,
            final Long memberId,
            final Long gameId
    ) {
        validateSelfMemberAccess(loggedInMemberId, memberId);

        final Member member = findMemberById(memberId);
        final Game game = findGameById(gameId);

        return RegistrationStatusResponse.from(member.findRegistationStatus(game));
    }

    public RegistrationStatusResponse findMemberRegistationStatusForCrew(
            final Long loggedInMemberId,
            final Long memberId,
            final Long crewId
    ) {
        validateSelfMemberAccess(loggedInMemberId, memberId);

        final Member member = findMemberById(memberId);
        final Crew crew = findCrewById(crewId);

        return RegistrationStatusResponse.from(member.findRegistationStatus(crew));
    }
}
