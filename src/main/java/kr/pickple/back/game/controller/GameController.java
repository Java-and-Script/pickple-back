package kr.pickple.back.game.controller;

import static org.springframework.http.HttpStatus.*;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import kr.pickple.back.auth.config.resolver.Login;
import kr.pickple.back.common.domain.RegistrationStatus;
import kr.pickple.back.game.domain.Category;
import kr.pickple.back.game.dto.request.GameCreateRequest;
import kr.pickple.back.game.dto.request.GameMemberRegistrationStatusUpdateRequest;
import kr.pickple.back.game.dto.request.MannerScoreReview;
import kr.pickple.back.game.dto.request.MannerScoreReviewsRequest;
import kr.pickple.back.game.dto.response.GameIdResponse;
import kr.pickple.back.game.dto.response.GameResponse;
import kr.pickple.back.game.dto.response.GamesAndLocationResponse;
import kr.pickple.back.game.service.GameFacadeService;
import kr.pickple.back.game.service.GameService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/games")
public class GameController {

    private final GameService gameService;
    private final GameFacadeService gameFacadeService;

    @PostMapping
    public ResponseEntity<GameIdResponse> createGame(
            @Login final Long loggedInMemberId,
            @Valid @RequestBody final GameCreateRequest gameCreateRequest
    ) {
        return ResponseEntity.status(CREATED)
                .body(gameService.createGame(gameCreateRequest, loggedInMemberId));
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<GameResponse> findGameDetailsById(
            @PathVariable final Long gameId
    ) {
        return ResponseEntity.status(OK)
                .body(gameService.findGameDetailsById(gameId));
    }

    @GetMapping
    public ResponseEntity<List<GameResponse>> findGamesByCategory(
            @RequestParam final Category category,
            @RequestParam final String value,
            final Pageable pageable
    ) {
        return ResponseEntity.status(OK)
                .body(gameService.findGamesByCategory(category, value, pageable));
    }

    @PostMapping("/{gameId}/members")
    public ResponseEntity<Void> registerGameMember(
            @Login final Long loggedInMemberId,
            @PathVariable final Long gameId
    ) {
        gameService.registerGameMember(gameId, loggedInMemberId);

        return ResponseEntity.status(NO_CONTENT)
                .build();
    }

    @GetMapping("/{gameId}/members")
    public ResponseEntity<GameResponse> findAllGameMembers(
            @Login final Long loggedInMemberId,
            @PathVariable final Long gameId,
            @RequestParam final RegistrationStatus status
    ) {
        return ResponseEntity.status(OK)
                .body(gameService.findAllGameMembers(loggedInMemberId, gameId, status));
    }

    @PatchMapping("/{gameId}/members/{memberId}")
    public ResponseEntity<Void> updateGameMemberRegistrationStatus(
            @Login final Long loggedInMemberId,
            @PathVariable final Long gameId,
            @PathVariable final Long memberId,
            @Valid @RequestBody final GameMemberRegistrationStatusUpdateRequest gameMemberRegistrationStatusUpdateRequest
    ) {
        gameService.updateGameMemberRegistrationStatus(
                loggedInMemberId,
                gameId,
                memberId,
                gameMemberRegistrationStatusUpdateRequest
        );

        return ResponseEntity.status(NO_CONTENT)
                .build();
    }

    @DeleteMapping("/{gameId}/members/{memberId}")
    public ResponseEntity<Void> deleteGameMember(
            @Login final Long loggedInMemberId,
            @PathVariable final Long gameId,
            @PathVariable final Long memberId
    ) {
        gameService.deleteGameMember(loggedInMemberId, gameId, memberId);

        return ResponseEntity.status(NO_CONTENT)
                .build();
    }

    @PatchMapping("/{gameId}/members/manner-scores")
    public ResponseEntity<Void> reviewMannerScores(
            @Login final Long loggedInMemberId,
            @PathVariable final Long gameId,
            @Valid @RequestBody final MannerScoreReviewsRequest mannerScoreReviewsRequest
    ) {
        final List<MannerScoreReview> mannerScoreReviews = mannerScoreReviewsRequest.getMannerScoreReviews();
        gameService.reviewMannerScores(loggedInMemberId, gameId, mannerScoreReviews);

        return ResponseEntity.status(NO_CONTENT)
                .build();
    }

    @GetMapping("/by-location")
    public ResponseEntity<List<GameResponse>> findGamesWithInDistance(
            @RequestParam final Double latitude,
            @RequestParam final Double longitude,
            @RequestParam final Double distance
    ) {
        return ResponseEntity.status(OK)
                .body(gameService.findGamesWithInDistance(latitude, longitude, distance));
    }

    @GetMapping("/by-address")
    public ResponseEntity<GamesAndLocationResponse> findGamesWithInAddress(
            @RequestParam final String addressDepth1,
            @RequestParam final String addressDepth2
    ) {
        return ResponseEntity.status(OK)
                .body(gameFacadeService.findGamesWithInAddress(addressDepth1, addressDepth2));
    }
}
