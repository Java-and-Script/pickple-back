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
import kr.pickple.back.auth.repository.RedisRepository;
import kr.pickple.back.chat.domain.ChatRoom;
import kr.pickple.back.chat.implement.ChatWriter;
import kr.pickple.back.game.domain.Category;
import kr.pickple.back.game.domain.Game;
import kr.pickple.back.game.domain.GameMember;
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
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.implement.MemberReader;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GameService {

    private final AddressReader addressReader;
    private final MemberReader memberReader;
    private final GameReader gameReader;
    private final GameWriter gameWriter;
    private final GameMemberWriter gameMemberWriter;
    private final ChatWriter chatWriter;
    private final RedisRepository redisRepository;

    /**
     * 게임 생성
     */
    @Transactional
    public GameIdResponse createGame(final GameCreateRequest gameCreateRequest, final Long loggedInMemberId) {
        final MainAddress mainAddress = addressReader.readMainAddressByAddressStrings(
                gameCreateRequest.getMainAddress());
        final NewGame newGame = GameRequestMapper.mapToNewGameDomain(gameCreateRequest, mainAddress);
        final Member host = memberReader.readByMemberId(loggedInMemberId);
        final ChatRoom chatRoom = chatWriter.createNewGroupRoom(
                GAME,
                makeGameChatRoomName(newGame),
                newGame.getMaxMemberCount()
        );

        newGame.assignHost(host);
        newGame.assignChatRoom(chatRoom);

        final Game game = gameWriter.create(newGame);
        final GameMember gameHost = gameMemberWriter.register(host, game);
        gameMemberWriter.updateMemberRegistrationStatus(gameHost, CONFIRMED);
        chatWriter.enterRoom(host, chatRoom);

        saveGameStatusUpdateEventToRedis(game);

        return GameIdResponse.from(game.getGameId());
    }

    private String makeGameChatRoomName(final NewGame newGame) {
        final String playDateFormat = newGame.getPlayDate().format(DateTimeFormatter.ofPattern("MM.dd"));
        final String addressDepth2Name = newGame.getAddressDepth2Name();

        return MessageFormat.format("{0} {1}", playDateFormat, addressDepth2Name);
    }

    private void saveGameStatusUpdateEventToRedis(final Game game) {
        final LocalDateTime gameCreatedDateTime = LocalDateTime.now();

        // 경기를 생성한 시각과 경기 시작 시간의 차
        final Long secondsBetweenCreatedAndPlay = getSecondsBetween(gameCreatedDateTime, game.getPlayStartDatetime());

        // 경기를 생성한 시각과 경기 종료 시간의 차
        final Long secondsBetweenCreatedAndEnd = getSecondsBetween(gameCreatedDateTime, game.getPlayEndDatetime());

        final String closedGameStatusUpdateKey = MessageFormat.format("game:{0}:{1}", CLOSED, game.getGameId());
        final String endedGameStatusUpdateKey = MessageFormat.format("game:{0}:{1}", ENDED, game.getGameId());

        redisRepository.saveHash(closedGameStatusUpdateKey, "", "", secondsBetweenCreatedAndPlay);
        redisRepository.saveHash(endedGameStatusUpdateKey, "", "", secondsBetweenCreatedAndEnd);
    }

    private Long getSecondsBetween(final LocalDateTime start, final LocalDateTime end) {
        return Duration.between(start, end).getSeconds();
    }

    /**
     * 게임 상세 조회
     */
    @Transactional
    public GameResponse findGameById(final Long gameId) {
        final Game game = gameReader.read(gameId);
        final List<Member> members = gameReader.readAllMembersByGameIdAndStatus(gameId, CONFIRMED);

        return GameResponseMapper.mapToGameResponseDto(game, members);
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
        final List<Game> games = gameReader.findGamesByAddress(address, pageable);

        return games.stream()
                .map(game -> GameResponseMapper.mapToGameResponseDto(
                                game,
                                gameReader.readAllMembersByGameIdAndStatus(game.getGameId(), CONFIRMED)
                        )
                ).toList();
    }

    /**
     * 중심 좌표(위도, 경도)로 부터 특정 거리(M) 까지의 게스트 모집글 조회
     */
    public List<GameResponse> findGamesWithInDistance(
            final Double latitude,
            final Double longitude,
            final Double distance
    ) {
        final List<Game> games = gameReader.findGamesWithInDistance(latitude, longitude, distance);

        return games.stream()
                .filter(Game::isNotEndedGame)
                .map(game -> GameResponseMapper.mapToGameResponseDto(
                                game,
                                gameReader.readAllMembersByGameIdAndStatus(game.getGameId(), CONFIRMED)
                        )
                ).toList();
    }
}
