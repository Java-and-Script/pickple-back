package kr.pickple.back.game.service;

import java.util.StringTokenizer;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import kr.pickple.back.game.domain.GameStatus;
import kr.pickple.back.game.implement.GameWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisExpirationListener implements MessageListener {
    
    private final GameWriter gameWriter;

    @Override
    public void onMessage(final Message message, final byte[] pattern) {
        final StringTokenizer stringTokenizer = new StringTokenizer(message.toString(), ":");
        final String keyName = stringTokenizer.nextToken();

        if (keyName.equals("game")) {
            final GameStatus gameStatus = GameStatus.valueOf(stringTokenizer.nextToken());
            final Long gameId = Long.parseLong(stringTokenizer.nextToken());

            gameWriter.updateMemberRegistrationStatus(gameStatus, gameId);
        }
    }
}
