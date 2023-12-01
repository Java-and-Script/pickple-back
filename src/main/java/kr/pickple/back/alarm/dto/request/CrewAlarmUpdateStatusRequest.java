package kr.pickple.back.alarm.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CrewAlarmUpdateStatusRequest {

    @NotNull(message = "크루 알림 읽음 여부는 필수입니다.")
    private Boolean isRead;

    public static CrewAlarmUpdateStatusRequest from(Boolean isRead) {
        return new CrewAlarmUpdateStatusRequest(isRead);
    }
}
