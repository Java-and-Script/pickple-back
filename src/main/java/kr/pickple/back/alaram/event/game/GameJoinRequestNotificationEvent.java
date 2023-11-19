package kr.pickple.back.alaram.event.game;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GameJoinRequestNotificationEvent {

    private final Long gameId;

    public Long getGameId() {
        return gameId;
    }
}
