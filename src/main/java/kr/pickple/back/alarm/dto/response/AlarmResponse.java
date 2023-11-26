package kr.pickple.back.alarm.dto.response;

import java.time.LocalDateTime;

public interface AlarmResponse {
    LocalDateTime getCreatedAt();
    Long getAlarmId();
}
