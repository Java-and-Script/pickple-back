package kr.pickple.back.game.service;

import static kr.pickple.back.common.domain.RegistrationStatus.*;
import static kr.pickple.back.game.exception.GameExceptionCode.*;
import static kr.pickple.back.member.exception.MemberExceptionCode.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.address.dto.response.MainAddressResponse;
import kr.pickple.back.address.service.AddressService;
import kr.pickple.back.common.domain.RegistrationStatus;
import kr.pickple.back.game.domain.Category;
import kr.pickple.back.game.domain.Game;
import kr.pickple.back.game.domain.GameMember;
import kr.pickple.back.game.dto.request.GameCreateRequest;
import kr.pickple.back.game.dto.request.GameMemberCreateRequest;
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

    private final AddressService addressService;
    private final GameRepository gameRepository;
    private final GameMemberRepository gameMemberRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public GameIdResponse createGame(final GameCreateRequest gameCreateRequest) {
        final Member host = findMemberById(gameCreateRequest.getHostId());
        final MainAddressResponse mainAddressResponse = addressService.findMainAddressByAddressStrings(
                gameCreateRequest.getMainAddress());

        final Game game = gameCreateRequest.toEntity(host, mainAddressResponse);
        final Game savedGame = gameRepository.save(game);
        savedGame.addGameMember(host);

        return GameIdResponse.from(savedGame.getId());
    }

    @Transactional
    public void registerGameMember(final Long gameId, final GameMemberCreateRequest gameMemberCreateRequest) {
        final Game game = findGameById(gameId);
        final Member member = findMemberById(gameMemberCreateRequest.getMemberId());

        game.addGameMember(member);
    }

    private Member findMemberById(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND, memberId));
    }

    public GameResponse findAllGameMembers(final Long gameId, final RegistrationStatus status) {
        final Game game = findGameById(gameId);

        return GameResponse.of(game, getMemberResponses(game, status));
    }

    public List<GameResponse> findGamesByCategory(final Category category, final String value,
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

        final Page<Game> games = gameRepository.findByAddressDepth1AndAddressDepth2(
                mainAddressResponse.getAddressDepth1(),
                mainAddressResponse.getAddressDepth2(),
                pageable
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
            final Long gameId,
            final Long memberId,
            final GameMemberRegistrationStatusUpdateRequest gameMemberRegistrationStatusUpdateRequest
    ) {
        final GameMember gameMember = findGameMemberByGameIdAndMemberId(gameId, memberId);
        final RegistrationStatus newStatus = gameMemberRegistrationStatusUpdateRequest.getStatus();

        gameMember.updateStatus(newStatus);
    }

    @Transactional
    public void deleteGameMember(final Long gameId, final Long memberId) {
        final GameMember gameMember = findGameMemberByGameIdAndMemberId(gameId, memberId);

        gameMemberRepository.delete(gameMember);
    }

    private GameMember findGameMemberByGameIdAndMemberId(final Long gameId, final Long memberId) {
        return gameMemberRepository.findByMemberIdAndGameId(memberId, gameId)
                .orElseThrow(() -> new GameException(GAME_MEMBER_NOT_FOUND, gameId, memberId));
    }

    @Transactional
    public void reviewMannerScores(final Long gameId, final List<MannerScoreReview> mannerScoreReviews) {
        Game game = findGameById(gameId);

        mannerScoreReviews.forEach(review -> {
            final Member reviewedMember = getReviewedMember(game, review.getMemberId());
            reviewedMember.updateMannerScore(review.getMannerScore());
        });
    }

    private Game findGameById(final Long gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new GameException(GAME_NOT_FOUND, gameId));
    }

    private Member getReviewedMember(final Game game, final Long reviewedMemberId) {
        return game.getMembersByStatus(CONFIRMED)
                .stream()
                .filter(confirmedMember -> confirmedMember.getId() == reviewedMemberId)
                .findFirst()
                .orElseThrow(() -> new GameException(GAME_MEMBER_NOT_FOUND, reviewedMemberId));
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
}
