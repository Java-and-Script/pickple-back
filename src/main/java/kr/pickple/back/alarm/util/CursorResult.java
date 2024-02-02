package kr.pickple.back.alarm.util;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CursorResult<T> {

    private List<T> alarmResponse;
    private Boolean hasNext;
    private Long cursorId;
}
