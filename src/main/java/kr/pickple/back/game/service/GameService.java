package kr.pickple.back.game.service;

import static kr.pickple.back.chat.domain.RoomType.*;
import static kr.pickple.back.common.domain.RegistrationStatus.*;
import static kr.pickple.back.game.domain.GameStatus.*;
import static kr.pickple.back.game.exception.GameExceptionCode.*;
import static kr.pickple.back.member.exception.MemberExceptionCode.*;

import java.text.MessageFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.locationtech.jts.geom.Point;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.address.dto.response.MainAddressResponse;
import kr.pickple.back.address.service.AddressService;
import kr.pickple.back.address.service.kakao.KakaoAddressSearchClient;
import kr.pickple.back.alarm.event.game.GameJoinRequestNotificationEvent;
import kr.pickple.back.alarm.event.game.GameMemberJoinedEvent;
import kr.pickple.back.alarm.event.game.GameMemberRejectedEvent;
import kr.pickple.back.auth.repository.RedisRepository;
import kr.pickple.back.chat.domain.ChatRoom;
import kr.pickple.back.chat.service.ChatMessageService;
import kr.pickple.back.chat.service.ChatRoomService;
import kr.pickple.back.common.domain.RegistrationStatus;
import kr.pickple.back.common.util.DateTimeUtil;
import kr.pickple.back.game.domain.Category;
import kr.pickple.back.game.domain.Game;
import kr.pickple.back.game.domain.GameMember;
import kr.pickple.back.game.domain.GameStatus;
import kr.pickple.back.game.dto.request.GameCreateRequest;
import kr.pickple.back.game.dto.request.GameMemberRegistrationStatusUpdateRequest;
import kr.pickple.back.game.dto.request.MannerScoreReview;
import kr.pickple.back.game.dto.response.GameIdResponse;
import kr.pickple.back.game.dto.response.GameResponse;
import kr.pickple.back.game.exception.GameException;
import kr.pickple.back.game.repository.GameMemberRepository;
import kr.pickple.back.game.repository.GameRepository;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.dto.response.MemberResponse;
import kr.pickple.back.member.exception.MemberException;
import kr.pickple.back.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GameService {

    private static final int REVIEW_POSSIBLE_DAYS = 7;

    private final GameRepository gameRepository;
    private final GameMemberRepository gameMemberRepository;
    private final MemberRepository memberRepository;
    private final KakaoAddressSearchClient kakaoAddressSearchClient;
    private final AddressService addressService;
    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;
    private final ApplicationEventPublisher eventPublisher;
    private final RedisRepository redisRepository;

    @Transactional
    public GameIdResponse createGame(final GameCreateRequest gameCreateRequest, final Long loggedInMemberId) {
        final Member host = findMemberById(loggedInMemberId);
        final Point point = kakaoAddressSearchClient.fetchAddress(
                gameCreateRequest.getMainAddress());
        final MainAddressResponse mainAddressResponse = addressService.findMainAddressByAddressStrings(
                gameCreateRequest.getMainAddress());

        final Game game = gameCreateRequest.toEntity(host, mainAddressResponse, point);
        game.addGameMember(host);

        final ChatRoom chatRoom = chatRoomService.saveNewChatRoom(host, makeGameRoomName(game), GAME);
        game.makeNewCrewChatRoom(chatRoom);

        final Long savedGameId = gameRepository.save(game).getId();
        saveGameStatusUpdateEventToRedis(game, savedGameId);

        return GameIdResponse.from(savedGameId);
    }

    private void saveGameStatusUpdateEventToRedis(final Game game, final Long savedGameId) {
        final LocalDateTime gameCreatedDateTime = LocalDateTime.now();

        // 경기를 생성한 시각과 경기 시작 시간의 차
        final Long secondsOfBetweenCreatedAndPlay = getSecondsBetweenCreatedAndPlay(gameCreatedDateTime, game);

        // 경기를 생성한 시각과 경기 종료 시간의 차
        final Long secondsOfBetweenCreatedAndEnd = getSecondsBetweenCreatedAndEnd(gameCreatedDateTime, game);

        final String closedGameStatusUpdateKey = makeGameStatusUpdateKey(CLOSED, savedGameId);
        final String endedGameStatusUpdateKey = makeGameStatusUpdateKey(ENDED, savedGameId);

        redisRepository.saveHash(closedGameStatusUpdateKey, "", "", secondsOfBetweenCreatedAndPlay);
        redisRepository.saveHash(endedGameStatusUpdateKey, "", "", secondsOfBetweenCreatedAndEnd);
    }

    private Long getSecondsBetweenCreatedAndPlay(final LocalDateTime gameCreatedDateTime, final Game game) {
        final LocalDateTime gamePlayDateTime = LocalDateTime.of(game.getPlayDate(), game.getPlayStartTime());

        return getSecondsBetween(gameCreatedDateTime, gamePlayDateTime);
    }

    private Long getSecondsBetweenCreatedAndEnd(final LocalDateTime gameCreatedDateTime, final Game game) {
        final LocalDateTime gameEndDateTime = game.getPlayEndDatetime();

        return getSecondsBetween(gameCreatedDateTime, gameEndDateTime);
    }

    private static long getSecondsBetween(
            final LocalDateTime gameCreatedDateTime,
            final LocalDateTime gamePlayDateTime
    ) {
        return Duration.between(gameCreatedDateTime, gamePlayDateTime)
                .getSeconds();
    }

    private String makeGameStatusUpdateKey(final GameStatus gameStatus, final Long id) {
        return String.format("game:%s:%d", gameStatus.toString(), id);
    }

    @Transactional
    public void updateGameStatus(final GameStatus gameStatus, final Long gameId) {
        final Game game = findGameById(gameId);
        game.updateGameStatus(gameStatus);
    }

    private String makeGameRoomName(final Game game) {
        final String playDateFormat = game.getPlayDate().format(DateTimeFormatter.ofPattern("MM.dd"));
        final String addressDepth2Name = game.getAddressDepth2().getName();

        return MessageFormat.format("{0} {1}", playDateFormat, addressDepth2Name);
    }

    @Transactional
    public GameResponse findGameDetailsById(final Long gameId) {
        final Game game = findGameById(gameId);
        final List<MemberResponse> memberResponses = game.getMembersByStatus(CONFIRMED)
                .stream()
                .map(MemberResponse::from)
                .toList();

        game.increaseViewCount();

        return GameResponse.of(game, memberResponses);
    }

    @Transactional
    public void registerGameMember(final Long gameId, final Long loggedInMemberId) {
        final Game game = findGameById(gameId);
        final Member member = findMemberById(loggedInMemberId);

        game.addGameMember(member);

        eventPublisher.publishEvent(GameJoinRequestNotificationEvent.builder()
                .gameId(gameId)
                .memberId(game.getHost().getId())
                .build());
    }

    private Game findGameById(final Long gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new GameException(GAME_NOT_FOUND, gameId));
    }

    public GameResponse findAllGameMembers(
            final Long loggedInMemberId,
            final Long gameId,
            final RegistrationStatus status
    ) {
        final GameMember gameMember = findGameMemberByGameIdAndMemberId(gameId, loggedInMemberId);
        final Game game = gameMember.getGame();
        final Member loggedInMember = gameMember.getMember();

        if (!game.isHost(loggedInMember) && status == WAITING) {
            throw new GameException(GAME_MEMBER_IS_NOT_HOST, loggedInMemberId);
        }

        return GameResponse.of(game, getMemberResponses(game, status));
    }

    public List<GameResponse> findGamesByCategory(
            final Category category,
            final String value,
            final Pageable pageable
    ) {
        return switch (category) {
            //현호 todo: playDate, positions 조건으로 조회하는 기능 추가 (MVP 미포함 기능)
            case ADDRESS -> findGamesByAddress(value, pageable);
            default -> throw new GameException(GAME_SEARCH_CATEGORY_IS_INVALID, category);
        };
    }

    private List<GameResponse> findGamesByAddress(final String address, final Pageable pageable) {
        final MainAddressResponse mainAddressResponse = addressService.findMainAddressByAddressStrings(address);

        final PageRequest pageRequest = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(
                        Sort.Order.asc("playDate"),
                        Sort.Order.asc("playStartTime"),
                        Sort.Order.asc("id")
                )
        );

        final Page<Game> games = gameRepository.findByAddressDepth1AndAddressDepth2AndStatusNot(
                mainAddressResponse.getAddressDepth1(),
                mainAddressResponse.getAddressDepth2(),
                GameStatus.ENDED,
                pageRequest
        );

        return games.stream()
                .map(game -> GameResponse.of(game, getMemberResponses(game, CONFIRMED)))
                .toList();
    }

    private List<MemberResponse> getMemberResponses(final Game game, final RegistrationStatus status) {
        return game.getMembersByStatus(status)
                .stream()
                .map(MemberResponse::from)
                .toList();
    }

    @Transactional
    public void updateGameMemberRegistrationStatus(
            final Long loggedInMemberId,
            final Long gameId,
            final Long memberId,
            final GameMemberRegistrationStatusUpdateRequest gameMemberRegistrationStatusUpdateRequest
    ) {
        final GameMember gameMember = findGameMemberByGameIdAndMemberId(gameId, memberId);
        final Game game = gameMember.getGame();

        validateIsHost(loggedInMemberId, game);

        final RegistrationStatus updateStatus = gameMemberRegistrationStatusUpdateRequest.getStatus();
        enterGameChatRoom(updateStatus, gameMember);

        gameMember.updateStatus(updateStatus);

        eventPublisher.publishEvent(GameMemberJoinedEvent.builder()
                .gameId(gameId)
                .memberId(memberId)
                .build());
    }

    private void validateIsHost(final Long loggedInMemberId, final Game game) {
        final Member loggedInMember = findMemberById(loggedInMemberId);

        if (!game.isHost(loggedInMember)) {
            throw new GameException(GAME_MEMBER_IS_NOT_HOST, loggedInMemberId);
        }
    }

    private void enterGameChatRoom(final RegistrationStatus updateStatus, final GameMember gameMember) {
        final RegistrationStatus nowStatus = gameMember.getStatus();

        if (nowStatus == WAITING && updateStatus == CONFIRMED) {
            chatMessageService.enterRoomAndSaveEnteringMessages(gameMember.getCrewChatRoom(), gameMember.getMember());
        }
    }

    @Transactional
    public void deleteGameMember(final Long loggedInMemberId, final Long gameId, final Long memberId) {
        final GameMember gameMember = findGameMemberByGameIdAndMemberId(gameId, memberId);
        final Game game = gameMember.getGame();
        final Member member = gameMember.getMember();
        final Member loggedInMember = findMemberById(loggedInMemberId);

        if (game.isHost(loggedInMember)) {
            validateIsHostSelfDeleted(loggedInMember, member);

            eventPublisher.publishEvent(GameMemberRejectedEvent.builder()
                    .gameId(gameId)
                    .memberId(memberId)
                    .build());

            deleteGameMember(gameMember);

            return;
        }

        if (loggedInMember.equals(member)) {
            cancelGameMember(gameMember);

            return;
        }

        throw new GameException(GAME_NOT_ALLOWED_TO_DELETE_GAME_MEMBER, loggedInMemberId);
    }

    private Member findMemberById(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND, memberId));
    }

    private void validateIsHostSelfDeleted(final Member loggedInMember, final Member member) {
        if (loggedInMember.equals(member)) {
            throw new GameException(GAME_HOST_CANNOT_BE_DELETED, loggedInMember.getId());
        }
    }

    private void cancelGameMember(final GameMember gameMember) {
        RegistrationStatus status = gameMember.getStatus();

        if (status != WAITING) {
            throw new GameException(GAME_MEMBER_STATUS_IS_NOT_WAITING, status);
        }

        deleteGameMember(gameMember);
    }

    private void deleteGameMember(final GameMember gameMember) {
        gameMemberRepository.delete(gameMember);
    }

    @Transactional
    public void reviewMannerScores(
            final Long loggedInMemberId,
            final Long gameId,
            final List<MannerScoreReview> mannerScoreReviews
    ) {
        final GameMember gameMember = gameMemberRepository.findByMemberIdAndGameIdAndStatus(
                        loggedInMemberId,
                        gameId,
                        CONFIRMED
                )
                .orElseThrow(() -> new GameException(GAME_MEMBER_NOT_FOUND, gameId, loggedInMemberId));

        if (gameMember.isAlreadyReviewDone()) {
            throw new GameException(GAME_MEMBER_NOT_ALLOWED_TO_REVIEW_AGAIN, loggedInMemberId);
        }

        final Game game = gameMember.getGame();
        final Member loggedInMember = gameMember.getMember();

        if (isNotReviewPeriod(game)) {
            throw new GameException(GAME_MEMBERS_CAN_REVIEW_DURING_POSSIBLE_PERIOD, game.getPlayDate(),
                    game.getPlayEndTime());
        }

        mannerScoreReviews.forEach(review -> {
            final Member reviewedMember = getReviewedMember(game, review.getMemberId());
            validateIsSelfReview(loggedInMember, reviewedMember);
            reviewedMember.updateMannerScore(review.getMannerScore());
        });

        gameMember.updateReviewDone();
    }

    private void validateIsSelfReview(final Member loggedInMember, final Member reviewedMember) {
        if (loggedInMember.equals(reviewedMember)) {
            throw new GameException(GAME_MEMBER_CANNOT_REVIEW_SELF, loggedInMember.getId(), reviewedMember.getId());
        }
    }

    private GameMember findGameMemberByGameIdAndMemberId(final Long gameId, final Long memberId) {
        return gameMemberRepository.findByMemberIdAndGameId(memberId, gameId)
                .orElseThrow(() -> new GameException(GAME_MEMBER_NOT_FOUND, gameId, memberId));
    }

    private Boolean isNotReviewPeriod(final Game game) {
        return isBeforeThanPlayEndTime(game) || isAfterReviewPossibleTime(game);
    }

    private Boolean isBeforeThanPlayEndTime(final Game game) {
        return DateTimeUtil.isAfterThanNow(game.getPlayEndDatetime());
    }

    private Boolean isAfterReviewPossibleTime(final Game game) {
        final LocalDateTime reviewDeadlineDatetime = game.getPlayEndDatetime().plusDays(REVIEW_POSSIBLE_DAYS);

        return DateTimeUtil.isEqualOrAfter(reviewDeadlineDatetime, LocalDateTime.now());
    }

    private Member getReviewedMember(final Game game, final Long reviewedMemberId) {
        return game.getMembersByStatus(CONFIRMED)
                .stream()
                .filter(confirmedMember -> confirmedMember.getId() == reviewedMemberId)
                .findFirst()
                .orElseThrow(() -> new GameException(GAME_MEMBER_NOT_FOUND, reviewedMemberId));
    }

    public List<GameResponse> findGamesWithInDistance(
            final Double latitude,
            final Double longitude,
            final Double distance
    ) {
        final List<Game> games = gameRepository.findGamesWithInDistance(latitude, longitude, distance);

        return games.stream()
                .filter(Game::isNotEndedGame)
                .map(game -> GameResponse.of(game, getMemberResponses(game, CONFIRMED)))
                .toList();
    }

    public List<GameResponse> findGamesWithInAddress(final MainAddressResponse mainAddressResponse) {
        final List<Game> games = gameRepository.findGamesWithInAddress(
                mainAddressResponse.getAddressDepth1(),
                mainAddressResponse.getAddressDepth2()
        );

        return games.stream()
                .filter(Game::isNotEndedGame)
                .map(game -> GameResponse.of(game, getMemberResponses(game, CONFIRMED)))
                .toList();
    }
}
