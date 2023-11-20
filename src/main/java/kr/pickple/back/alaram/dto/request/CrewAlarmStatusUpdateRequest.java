package kr.pickple.back.alaram.dto.request;

import jakarta.validation.constraints.NotNull;
import kr.pickple.back.alaram.domain.AlarmStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CrewAlarmStatusUpdateRequest {

    @NotNull(message = "크루 알람 읽음 상태 여부는 필수입니다.")
    private AlarmStatus isRead;
}
