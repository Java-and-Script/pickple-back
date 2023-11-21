package kr.pickple.back.alarm.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import kr.pickple.back.alarm.domain.AlarmStatus;
import kr.pickple.back.alarm.domain.AlarmType;
import kr.pickple.back.alarm.domain.CrewAlarm;
import kr.pickple.back.crew.domain.Crew;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@JsonSerialize
@RequiredArgsConstructor
public class CrewAlarmResponse {

    private final Long id;
    private final Long crewId;
    private final String crewName;
    private final LocalDateTime createdAt;
    private final AlarmStatus isRead;
    private final AlarmType alarmType;

    public static CrewAlarmResponse of(final CrewAlarm crewAlarm) {
        final Crew crew = crewAlarm.getCrew();

        return CrewAlarmResponse.builder()
                .id(crewAlarm.getId())
                .crewId(crew.getId())
                .crewName(crewAlarm.getCrew().getName())
                .createdAt(crewAlarm.getCreatedAt())
                .isRead(crewAlarm.getIsRead())
                .alarmType(crewAlarm.getAlarmType())
                .build();
    }
}
