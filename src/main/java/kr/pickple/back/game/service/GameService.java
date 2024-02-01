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
import kr.pickple.back.member.domain.MemberPosition;
import kr.pickple.back.member.dto.response.MemberResponse;
import kr.pickple.back.member.exception.MemberException;
import kr.pickple.back.member.repository.MemberPositionRepository;
import kr.pickple.back.member.repository.MemberRepository;
import kr.pickple.back.position.domain.Position;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GameService {

	private final GameRepository gameRepository;
	private final MemberPositionRepository memberPositionRepository;
	private final MemberRepository memberRepository;
	private final KakaoAddressSearchClient kakaoAddressSearchClient;
	private final AddressService addressService;
	private final ChatRoomService chatRoomService;
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
				.map(member -> MemberResponse.of(member, getPositions(member)))
				.toList();

		game.increaseViewCount();

		return GameResponse.of(game, memberResponses);
	}

	private Game findGameById(final Long gameId) {
		return gameRepository.findById(gameId)
				.orElseThrow(() -> new GameException(GAME_NOT_FOUND, gameId));
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
				.map(member -> MemberResponse.of(member, getPositions(member)))
				.toList();
	}

	private Member findMemberById(final Long memberId) {
		return memberRepository.findById(memberId)
				.orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND, memberId));
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

	private List<Position> getPositions(final Member member) {
		final List<MemberPosition> memberPositions = memberPositionRepository.findAllByMemberId(
				member.getId());

		return Position.from(memberPositions);
	}
}
