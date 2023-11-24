package kr.pickple.back.alarm.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import kr.pickple.back.alarm.domain.AlarmStatus;
import kr.pickple.back.alarm.domain.CrewAlarm;
import kr.pickple.back.alarm.domain.CrewAlarmType;
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

    private final Long crewAlarmId;
    private final Long crewId;
    private final String crewName;
    private final String crewProfileImageUrl;
    private final LocalDateTime createdAt;
    private final AlarmStatus isRead;
    private final CrewAlarmType crewAlarmMessage;

    public static CrewAlarmResponse from(final CrewAlarm crewAlarm) {
        final Crew crew = crewAlarm.getCrew();

        return CrewAlarmResponse.builder()
                .crewAlarmId(crewAlarm.getId())
                .crewId(crew.getId())
                .crewName(crewAlarm.getCrew().getName())
                .crewProfileImageUrl(crew.getProfileImageUrl())
                .createdAt(crewAlarm.getCreatedAt())
                .isRead(crewAlarm.getIsRead())
                .crewAlarmMessage(crewAlarm.getCrewAlarmType())
                .build();
    }
}
