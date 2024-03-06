package kr.pickple.back.game.service;

import static kr.pickple.back.chat.domain.RoomType.*;
import static kr.pickple.back.common.domain.RegistrationStatus.*;
import static kr.pickple.back.game.domain.GameStatus.*;
import static kr.pickple.back.game.exception.GameExceptionCode.*;

import java.text.MessageFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.address.dto.response.MainAddress;
import kr.pickple.back.address.implement.AddressReader;
import kr.pickple.back.address.service.kakao.KakaoAddressSearchClient;
import kr.pickple.back.auth.repository.RedisRepository;
import kr.pickple.back.chat.domain.ChatRoom;
import kr.pickple.back.chat.service.ChatRoomService;
import kr.pickple.back.game.domain.Category;
import kr.pickple.back.game.domain.GameDomain;
import kr.pickple.back.game.domain.GameMemberDomain;
import kr.pickple.back.game.domain.GameStatus;
import kr.pickple.back.game.domain.NewGame;
import kr.pickple.back.game.dto.mapper.GameRequestMapper;
import kr.pickple.back.game.dto.mapper.GameResponseMapper;
import kr.pickple.back.game.dto.request.GameCreateRequest;
import kr.pickple.back.game.dto.response.GameIdResponse;
import kr.pickple.back.game.dto.response.GameResponse;
import kr.pickple.back.game.exception.GameException;
import kr.pickple.back.game.implement.GameMemberWriter;
import kr.pickple.back.game.implement.GameReader;
import kr.pickple.back.game.implement.GameWriter;
import kr.pickple.back.game.repository.GameMemberRepository;
import kr.pickple.back.game.repository.GamePositionRepository;
import kr.pickple.back.game.repository.GameRepository;
import kr.pickple.back.member.domain.MemberDomain;
import kr.pickple.back.member.implement.MemberReader;
import kr.pickple.back.member.repository.MemberPositionRepository;
import kr.pickple.back.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GameService {

    private final AddressReader addressReader;

    private final GameRepository gameRepository;
    private final MemberPositionRepository memberPositionRepository;
    private final MemberRepository memberRepository;
    private final KakaoAddressSearchClient kakaoAddressSearchClient;
    private final ChatRoomService chatRoomService;
    private final RedisRepository redisRepository;
    private final GamePositionRepository gamePositionRepository;
    private final GameMemberRepository gameMemberRepository;
    private final GameReader gameReader;
    private final MemberReader memberReader;
    private final GameWriter gameWriter;
    private final GameMemberWriter gameMemberWriter;

    /**
     * 게임 생성
     */
    @Transactional
    public GameIdResponse createGame(final GameCreateRequest gameCreateRequest, final Long loggedInMemberId) {
        final MainAddress mainAddress = addressReader.readMainAddressByAddressStrings(gameCreateRequest.getMainAddress());
        final NewGame newGame = GameRequestMapper.mapToNewGameDomain(gameCreateRequest, mainAddress);

        final MemberDomain host = memberReader.readByMemberId(loggedInMemberId);

        final ChatRoom chatRoom = chatRoomService.saveNewChatRoom(host, makeGameRoomName(newGame), GAME);
        chatRoom.updateMaxMemberCount(newGame.getMaxMemberCount());

        newGame.assignHost(host);
        newGame.assignChatRoom(chatRoom);

        final GameDomain game = gameWriter.create(newGame);

        final GameMemberDomain gameHost = gameMemberWriter.register(host, game);
        gameMemberWriter.updateMemberRegistrationStatus(gameHost, CONFIRMED);

        saveGameStatusUpdateEventToRedis(game);

        return GameIdResponse.from(game.getGameId());
    }

    private String makeGameRoomName(final NewGame newGame) {
        final String playDateFormat = newGame.getPlayDate().format(DateTimeFormatter.ofPattern("MM.dd"));
        final String addressDepth2Name = newGame.getAddressDepth2Name();

        return MessageFormat.format("{0} {1}", playDateFormat, addressDepth2Name);
    }

    private void saveGameStatusUpdateEventToRedis(final GameDomain gameDomain) {
        final LocalDateTime gameCreatedDateTime = LocalDateTime.now();

        // 경기를 생성한 시각과 경기 시작 시간의 차
        final Long secondsOfBetweenCreatedAndPlay = getSecondsBetweenCreatedAndPlay(gameCreatedDateTime, gameDomain);

        // 경기를 생성한 시각과 경기 종료 시간의 차
        final Long secondsOfBetweenCreatedAndEnd = getSecondsBetweenCreatedAndEnd(gameCreatedDateTime, gameDomain);

        final String closedGameStatusUpdateKey = makeGameStatusUpdateKey(CLOSED, gameDomain.getGameId());
        final String endedGameStatusUpdateKey = makeGameStatusUpdateKey(ENDED, gameDomain.getGameId());

        redisRepository.saveHash(closedGameStatusUpdateKey, "", "", secondsOfBetweenCreatedAndPlay);
        redisRepository.saveHash(endedGameStatusUpdateKey, "", "", secondsOfBetweenCreatedAndEnd);
    }

    private Long getSecondsBetweenCreatedAndPlay(final LocalDateTime gameCreatedDateTime, final GameDomain gameDomain) {
        final LocalDateTime gamePlayDateTime = LocalDateTime.of(gameDomain.getPlayDate(), gameDomain.getPlayStartTime());

        return getSecondsBetween(gameCreatedDateTime, gamePlayDateTime);
    }

    private Long getSecondsBetweenCreatedAndEnd(final LocalDateTime gameCreatedDateTime, final GameDomain gameDomain) {
        final LocalDateTime gameEndDateTime = gameDomain.getPlayEndDatetime();

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

    /**
     * 게임 상세 조회
     */
    @Transactional
    public GameResponse findGameById(final Long gameId) {
        final GameDomain gameDomain = gameReader.read(gameId);
        final List<MemberDomain> members = gameReader.readAllMembersByGameIdAndStatus(gameId, CONFIRMED);

        return GameResponseMapper.mapToGameResponseDto(gameDomain, members);
    }

    /**
     * 게임 카테고리별 조회
     */
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

    /**
     * 주소별 게스트 모집글 조회
     */
    private List<GameResponse> findGamesByAddress(final String address, final Pageable pageable) {
        final List<GameDomain> gameDomains = gameReader.findGamesByAddress(address, pageable);

        return gameDomains.stream()
                .map(gameDomain -> GameResponseMapper.mapToGameResponseDto(
                                gameDomain,
                                gameReader.readAllMembersByGameIdAndStatus(gameDomain.getGameId(), CONFIRMED)
                        )
                )
                .toList();
    }

    /**
     * 특정 지역의 게스트 모집글 조회
     */
    public List<GameResponse> findGamesWithInAddress(final MainAddress mainAddress) {
        final List<GameDomain> gameDomains = gameReader.findGamesWithInAddress(mainAddress);

        return gameDomains.stream()
                .map(gameDomain -> GameResponseMapper.mapToGameResponseDto(
                                gameDomain,
                                gameReader.readAllMembersByGameIdAndStatus(gameDomain.getGameId(), CONFIRMED)
                        )
                )
                .toList();
    }

    /**
     * 중심 좌표(위도, 경도)로 부터 특정 거리(M) 까지의 게스트 모집글 조회
     */
    public List<GameResponse> findGamesWithInDistance(
            final Double latitude,
            final Double longitude,
            final Double distance
    ) {
        final List<GameDomain> gameDomains = gameReader.findGamesWithInDistance(latitude, longitude, distance);

        return gameDomains.stream()
                .filter(GameDomain::isNotEndedGame)
                .map(gameDomain -> GameResponseMapper.mapToGameResponseDto(
                                gameDomain,
                                gameReader.readAllMembersByGameIdAndStatus(gameDomain.getGameId(), CONFIRMED)
                        )
                )
                .toList();
    }
}
