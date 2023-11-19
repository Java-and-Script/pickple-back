package kr.pickple.back.alaram.handler;

import kr.pickple.back.alaram.domain.GameAlaram;
import kr.pickple.back.alaram.event.game.GameJoinRequestNotificationEvent;
import kr.pickple.back.alaram.event.game.GameMemberJoinedEvent;
import kr.pickple.back.alaram.event.game.GameMemberRejectedEvent;
import kr.pickple.back.alaram.service.GameAlaramService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GameAlaramEventHandler {

    private final GameAlaramService gameAlaramService;

    @EventListener
    public void sendAlaramToGameHost(final GameJoinRequestNotificationEvent gameJoinRequestNotificationEvent) {
        final GameAlaram gameAlaram = gameAlaramService.createAlaram(gameJoinRequestNotificationEvent); //알람 생성
        gameAlaramService.emitMessage(gameAlaram); //SSE로 알람 메시지 전송
    }

    @EventListener
    public void sendAlaramToGameMemberOnJoin(final GameMemberJoinedEvent gameMemberJoinedEvent) {
        final GameAlaram gameAlaram = gameAlaramService.createAlaram(gameMemberJoinedEvent); //알람 생성
        gameAlaramService.emitMessage(gameAlaram); //SSE로 알람 메시지 전송
    }

    @EventListener
    public void sendAlaramToGameMemberOnRejection(final GameMemberRejectedEvent gameMemberRejectedEvent) {
        final GameAlaram gameAlaram = gameAlaramService.createAlaram(gameMemberRejectedEvent); //알람 생성
        gameAlaramService.emitMessage(gameAlaram); //SSE로 알람 메시지 전송
    }
}
