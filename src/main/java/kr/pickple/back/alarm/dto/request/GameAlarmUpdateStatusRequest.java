package kr.pickple.back.alarm.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameAlarmUpdateStatusRequest {

    @NotNull(message = "게임 알림 읽음 여부는 필수입니다.")
    private Boolean isRead;

    public static GameAlarmUpdateStatusRequest from(Boolean isRead) {
        return new GameAlarmUpdateStatusRequest(isRead);
    }
}
