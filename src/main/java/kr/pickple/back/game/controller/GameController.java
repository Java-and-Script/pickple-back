package kr.pickple.back.game.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.pickple.back.game.dto.GameCreateRequest;
import kr.pickple.back.game.dto.GameIdResponse;
import kr.pickple.back.game.service.GameService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/games")
public class GameController {

    private final GameService gameService;

    @PostMapping
    public GameIdResponse post(@RequestBody GameCreateRequest gameCreateRequest) {
        return gameService.createGame(gameCreateRequest);
    }
}
