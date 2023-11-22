package kr.pickple.back.alarm.event.game;

import kr.pickple.back.member.domain.Member;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GameJoinRequestNotificationEvent {

    private final Long gameId;
    private final Member host;

    public Long getGameId() {
        return gameId;
    }

    public Member getHost() {
        return host;
    }
}
