package kr.pickple.back.alarm.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CursorResult<T> {

    private List<T> alarmResponse;
    private Boolean hasNext;
}
