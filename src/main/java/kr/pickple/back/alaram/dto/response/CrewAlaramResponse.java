package kr.pickple.back.alaram.dto.response;

import kr.pickple.back.alaram.domain.AlarmStatus;
import kr.pickple.back.alaram.domain.AlarmType;
import kr.pickple.back.alaram.domain.CrewAlarm;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@RequiredArgsConstructor
public class CrewAlaramResponse {

    private final CrewAlarm crewAlarm;
    private final Long id;
    private final String crewName;
    private final LocalDateTime createdAt;
    private final AlarmStatus isRead;
    private final AlarmType alarmType;

    public static CrewAlaramResponse of(final CrewAlarm crewAlarm) {
        return CrewAlaramResponse.builder()
                .id(crewAlarm.getId())
                .crewName(crewAlarm.getCrew().getName())
                .createdAt(crewAlarm.getCreatedAt())
                .isRead(crewAlarm.getIsRead())
                .alarmType(crewAlarm.getAlarmType())
                .build();
    }
}
