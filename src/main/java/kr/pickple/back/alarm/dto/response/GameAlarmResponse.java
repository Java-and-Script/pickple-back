package kr.pickple.back.alarm.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import kr.pickple.back.alarm.domain.AlarmStatus;
import kr.pickple.back.alarm.domain.AlarmType;
import kr.pickple.back.alarm.domain.GameAlarm;
import kr.pickple.back.game.domain.Game;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Builder
@JsonSerialize //text/event-stream'의 MIME 타입이기 때문에 JsonSerialize로 Json형식으로 직렬화해야 함
@RequiredArgsConstructor
public class GameAlarmResponse {

    private final Long id;
    private final Long gameId;
    private final String mainAddress;
    private final LocalDateTime createdAt;
    private final LocalDate playDate;
    private final LocalTime playStartTime;
    private final Integer playTimeMinutes;
    private final AlarmStatus isRead;
    private final AlarmType alarmType;

    public static GameAlarmResponse of(final GameAlarm gameAlarm) {
        final Game game = gameAlarm.getGame();

        return GameAlarmResponse.builder()
                .id(gameAlarm.getId())
                .gameId(game.getId())
                .mainAddress(gameAlarm.getGame().getMainAddress())
                .createdAt(gameAlarm.getCreatedAt())
                .playDate(gameAlarm.getGame().getPlayDate())
                .playStartTime(gameAlarm.getGame().getPlayStartTime())
                .playTimeMinutes(gameAlarm.getGame().getPlayTimeMinutes())
                .isRead(gameAlarm.getIsRead())
                .alarmType(gameAlarm.getAlarmType())
                .build();
    }
}
