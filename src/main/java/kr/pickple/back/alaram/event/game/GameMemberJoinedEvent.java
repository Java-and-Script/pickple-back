package kr.pickple.back.alaram.event.game;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GameMemberJoinedEvent {

    private final Long gameId;
    private final Long memberId;

    public Long getGameId() {
        return gameId;
    }

    public Long getMemberId() {
        return memberId;
    }
}
