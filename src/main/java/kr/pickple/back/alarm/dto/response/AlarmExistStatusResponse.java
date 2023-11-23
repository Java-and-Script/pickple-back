package kr.pickple.back.alarm.dto.response;

import kr.pickple.back.alarm.domain.AlarmExistsStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AlarmExistStatusResponse {

    private final boolean unread;

    public static AlarmExistStatusResponse of(final AlarmExistsStatus status) {
        return AlarmExistStatusResponse.builder()
                .unread(status.getBooleanValue())
                .build();
    }
}
