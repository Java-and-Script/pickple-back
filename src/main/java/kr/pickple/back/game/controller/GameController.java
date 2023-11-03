package kr.pickple.back.game.controller;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import kr.pickple.back.game.dto.request.GameCreateRequest;
import kr.pickple.back.game.dto.request.GameMemberCreateRequest;
import kr.pickple.back.game.dto.response.GameIdResponse;
import kr.pickple.back.game.service.GameService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/games")
public class GameController {

    private final GameService gameService;

    @PostMapping
    public ResponseEntity<GameIdResponse> createGame(@Valid @RequestBody final GameCreateRequest gameCreateRequest) {
        return ResponseEntity.status(CREATED)
                .body(gameService.createGame(gameCreateRequest));
    }

    @PostMapping("/{gameId}/members")
    public ResponseEntity<Void> registerGameMember(
            @PathVariable final Long gameId,
            @Valid @RequestBody final GameMemberCreateRequest gameMemberCreateRequest
    ) {
        gameService.registerGameMember(gameId, gameMemberCreateRequest);

        return ResponseEntity.status(NO_CONTENT)
                .build();
    }
}
