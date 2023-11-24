package kr.pickple.back.alarm.dto.response;

import java.time.LocalDateTime;

//CrewAlarmResponse나 GameAlarmResponse는 서로 다른 클래스임으로, 하나의 리스트에 넣기 위해 공통 인터페이스 생성
public interface AlarmResponse {
     LocalDateTime getCreatedAt();

     Long getId();
}
