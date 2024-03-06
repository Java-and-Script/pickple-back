package kr.pickple.back.alarm.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import kr.pickple.back.alarm.domain.GameAlarm;
import kr.pickple.back.alarm.domain.GameAlarmType;
import kr.pickple.back.game.repository.entity.GameEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@JsonSerialize
@RequiredArgsConstructor
public class GameAlarmResponse implements AlarmResponse {

    private final Long gameAlarmId;
    private final Long gameId;
    private final String mainAddress;
    private final LocalDateTime createdAt;
    private final LocalDate playDate;
    private final LocalTime playStartTime;
    private final Integer playTimeMinutes;
    private final Boolean isRead;
    private final GameAlarmType gameAlarmMessage;

    public static GameAlarmResponse from(final GameAlarm gameAlarm) {
        final GameEntity gameEntity = gameAlarm.getGameEntity();

        return GameAlarmResponse.builder()
                .gameAlarmId(gameAlarm.getId())
                .gameId(gameEntity.getId())
                .mainAddress(gameAlarm.getGameEntity().getMainAddress())
                .createdAt(gameAlarm.getCreatedAt())
                .playDate(gameAlarm.getGameEntity().getPlayDate())
                .playStartTime(gameAlarm.getGameEntity().getPlayStartTime())
                .playTimeMinutes(gameAlarm.getGameEntity().getPlayTimeMinutes())
                .isRead(gameAlarm.getIsRead())
                .gameAlarmMessage(gameAlarm.getGameAlarmType())
                .build();
    }

    @Override
    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    @Override
    public Long getAlarmId() {
        return gameAlarmId;
    }
}
