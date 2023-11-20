package kr.pickple.back.alaram.dto.response;

import kr.pickple.back.alaram.domain.AlarmStatus;
import kr.pickple.back.alaram.domain.AlarmType;
import kr.pickple.back.alaram.domain.GameAlarm;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Builder
@RequiredArgsConstructor
public class GameAlaramResponse {

    private final GameAlarm gameAlarm;
    private final Long id;
    private final String mainAddress;
    private final LocalDateTime createdAt;
    private final LocalDate playDate;
    private final LocalTime playStartTime;
    private final Integer playTimeMinutes;
    private final AlarmStatus isRead;
    private final AlarmType alarmType;

    public static GameAlaramResponse of(final GameAlarm gameAlarm) {
        return GameAlaramResponse.builder()
                .id(gameAlarm.getId())
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
