package kr.pickple.back.alarm.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import kr.pickple.back.alarm.domain.AlarmStatus;
import kr.pickple.back.alarm.domain.GameAlarm;
import kr.pickple.back.alarm.domain.GameAlarmType;
import kr.pickple.back.game.domain.Game;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Builder
@JsonSerialize
@RequiredArgsConstructor
public class GameAlarmResponse implements AlarmResponse {

    private final Long id;
    private final Long gameId;
    private final String mainAddress;
    private final LocalDateTime createdAt;
    private final LocalDate playDate;
    private final LocalTime playStartTime;
    private final Integer playTimeMinutes;
    private final AlarmStatus isRead;
    private final GameAlarmType gameAlarmType;

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
                .gameAlarmType(gameAlarm.getGameAlarmType())
                .build();
    }

    @Override
    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    @Override
    public Long getId() {
        return this.id;
    }
}
