package kr.pickple.back.game.controller;

import static org.springframework.http.HttpStatus.*;

import java.util.List;

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
import kr.pickple.back.game.dto.request.GameCreateRequest;
import kr.pickple.back.game.dto.request.GameMemberCreateRequest;
import kr.pickple.back.game.dto.request.GameMemberRegistrationStatusUpdateRequest;
import kr.pickple.back.game.dto.request.MannerScoreReview;
import kr.pickple.back.game.dto.request.MannerScoreReviewsRequest;
import kr.pickple.back.game.dto.response.GameIdResponse;
import kr.pickple.back.game.dto.response.GameResponse;
import kr.pickple.back.game.service.GameService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/games")
public class GameController {

    private final GameService gameService;

    @PostMapping
    public ResponseEntity<GameIdResponse> createGame(
            @Login final Long loggedInMemberId,
            @Valid @RequestBody final GameCreateRequest gameCreateRequest
    ) {
        return ResponseEntity.status(CREATED)
                .body(gameService.createGame(gameCreateRequest));
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<GameResponse> findGameDetailsById(
            @PathVariable final Long gameId
    ) {
        return ResponseEntity.status(OK)
                .body(gameService.findGameDetailsById(gameId));
    }

    @PostMapping("/{gameId}/members")
    public ResponseEntity<Void> registerGameMember(
            @Login final Long loggedInMemberId,
            @PathVariable final Long gameId,
            @Valid @RequestBody final GameMemberCreateRequest gameMemberCreateRequest
    ) {
        gameService.registerGameMember(gameId, gameMemberCreateRequest);

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
                .body(gameService.findAllGameMembers(gameId, status));
    }

    @PatchMapping("/{gameId}/members/{memberId}")
    public ResponseEntity<Void> updateGameMemberRegistrationStatus(
            @Login final Long loggedInMemberId,
            @PathVariable final Long gameId,
            @PathVariable final Long memberId,
            @Valid @RequestBody final GameMemberRegistrationStatusUpdateRequest gameMemberRegistrationStatusUpdateRequest
    ) {
        gameService.updateGameMemberRegistrationStatus(gameId, memberId, gameMemberRegistrationStatusUpdateRequest);

        return ResponseEntity.status(NO_CONTENT)
                .build();
    }

    @DeleteMapping("/{gameId}/members/{memberId}")
    public ResponseEntity<Void> deleteGameMember(
            @Login final Long loggedInMemberId,
            @PathVariable final Long gameId,
            @PathVariable final Long memberId
    ) {
        gameService.deleteGameMember(gameId, memberId);

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
        gameService.reviewMannerScores(gameId, mannerScoreReviews);

        return ResponseEntity.status(NO_CONTENT)
                .build();
    }
}
