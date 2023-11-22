package kr.pickple.back.alarm.dto.request;

import jakarta.validation.constraints.NotNull;
import kr.pickple.back.alarm.domain.AlarmStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameAlarmUpdateStatusRequest {

    @NotNull(message = "게임 알림 읽음 여부는 필수입니다.")
    private AlarmStatus isRead;
}
