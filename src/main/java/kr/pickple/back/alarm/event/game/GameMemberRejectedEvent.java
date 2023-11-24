package kr.pickple.back.alarm.event.game;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class GameMemberRejectedEvent {

    private final Long gameId;
    private final Long memberId;
}
