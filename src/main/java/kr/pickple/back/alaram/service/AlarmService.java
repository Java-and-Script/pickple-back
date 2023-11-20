package kr.pickple.back.alaram.service;

import kr.pickple.back.alaram.domain.AlarmExistsStatus;
import kr.pickple.back.alaram.domain.CrewAlarm;
import kr.pickple.back.alaram.domain.GameAlarm;
import kr.pickple.back.alaram.exception.AlarmException;
import kr.pickple.back.alaram.exception.AlarmExceptionCode;
import kr.pickple.back.alaram.util.SseEmitters;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.exception.MemberException;
import kr.pickple.back.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

import static kr.pickple.back.alaram.domain.AlarmExistsStatus.EXISTS;
import static kr.pickple.back.alaram.domain.AlarmExistsStatus.NOT_EXISTS;
import static kr.pickple.back.member.exception.MemberExceptionCode.MEMBER_NOT_FOUND;

//SSE 연결 로직에서는 @Transcational금지
@Service
@RequiredArgsConstructor
public class AlarmService {

    private final GameAlarmService gameAlarmService;
    private final CrewAlarmService crewAlarmService;
    private final MemberRepository memberRepository;
    private final SseEmitters sseEmitters;

    //1.SSE 연결 - 이벤트가 발생 된 후, 30초동안 지속 연결
    public SseEmitter subscribeToSse(final Long loggedInMemberId) {
        final Member member = findMemberById(loggedInMemberId);
        final SseEmitter emitter = new SseEmitter(30 * 1000L); //30초안 sse 연결 지속
        sseEmitters.add(loggedInMemberId, emitter);

        try {
            emitter.send(SseEmitter.event()
                    .name("AlaramSseConnect") // 해당 이벤트의 이름 지정
                    .data("사용자에 대한 알람 SSE 연결이 정상적으로 처리되었습니다.")); // 503 에러 방지를 위한 더미 데이터
        } catch (IOException e) {
            sseEmitters.remove(loggedInMemberId);
            emitter.completeWithError(e);
        }


        return emitter;
    }

    //특정 사용자(loggedInMemberId)에게 이벤트(event)를 전송하는 역할을 함
    //SseEmitters에서 해당 사용자의 SseEmitter를 가져오고, 이를 통해 이벤트를 클라이언트에게 전송
    //특정 이벤트가 발생했을 때 해당 이벤트를 클라이언트에게 실시간으로 전달하는데 사용됨
    //새로운 알림이 도착했을 때 이를 클라이언트에게 실시간으로 알려주는 등의 기능을 구현하고자 할 때 notify 메서드를 사용
    public void notify(final Long loggedInMemberId, final Object event) {
        final SseEmitter emitter = sseEmitters.get(loggedInMemberId);
        if (emitter != null) {
            try {
                emitter.send(event);
            } catch (IOException e) {
                sseEmitters.remove(loggedInMemberId);
                emitter.completeWithError(e);
            }
        }
    }

    //모든 알람 찾기 모두 - 각 알림에서 생성일 순으로 조회

//    public static List<AlaramProfileResponse> findAllAlarms() {
//        return null;
//    }

    //특정 알림 읽음, 읽지 않음 상태 확인
    //1. memberId를 통해, 각 알림의 Repository에서 읽지 않은 알림이 있는지 판별
    //2. 크루 관련 알람 -> 해당 사용자의 크루 관련 알람의 읽지 않은 상태 확인(checkUnreadCrewAlarm)메소드 -> 있다면 True반환
    //2. 게임 관련 알람 -> 해당 사용자의 게임 관련 알람의 읽지 않은 상태 확인( checkUnreadGameAlarm)메소드 -> 있다면 True반환
    //3. 두 알림 중 하나라도 읽지 않은 상태가 있다면 True를 , 두 알림 중 모두 읽은 상태라면 False를 반환
    public AlarmExistsStatus checkUnReadAlarms(final Long loggedInMemberId) {
        final boolean existsUnreadCrewAlarm = crewAlarmService.checkUnreadCrewAlarm(loggedInMemberId);
        final boolean existsUnreadGameAlarm = gameAlarmService.checkUnreadGameAlarm(loggedInMemberId);

        return existsUnreadCrewAlarm || existsUnreadGameAlarm ? EXISTS : NOT_EXISTS;
    }

    //특정 알림 수정 - 해당 id가 각 크루,게임 알림 중, 어떤 것인지 판별하고 읽음 처리
    //1. alarmId를 통해, 들어온 알람이 어떤 알람인지 판별
    //2. 크루 관련 알람 -> 해당 크루 관련 알람을 찾음(findById)메소드 -> 상태 수정(updateById) 메소드
    //3. 게임 관련 알람 -> 해당 게임 관련 알람을 찾음(findById)메소드 -> 상태 수정(updateById) 메소드
    public void updateAlarmById(final Long loggedInMemberId, final Long alarmId, final String isRead) {
        //1.해당 회원의 ID가 있는지 체크
        final Member member = findMemberById(loggedInMemberId);

        //2.해당 알람이 어떤 알람인지 판별
        final GameAlarm gameAlarm = gameAlarmService.findGameAlarmById(alarmId);
        final CrewAlarm crewAlarm = crewAlarmService.findCrewAlramById(alarmId);

        //3.해당 알람이 어떤 알람인지 확인하고, 상태 수정
        if (gameAlarm != null) {
            gameAlarmService.updateGameAlarmStatus(alarmId,isRead);
        }

        if (crewAlarm != null) {
            crewAlarmService.updateCrewAlaramStatus(alarmId,isRead);
        }
        //아니면
        throw new AlarmException(AlarmExceptionCode.ALARM_NOT_FOUND, alarmId);
    }

    //모든 알림 삭제 - 모든 알림들을 삭제함
    public void deleteAllAlarms(final Long loggedInMemberId) {
        //1.해당 회원의 ID가 있는지 체크
        final Member member = findMemberById(loggedInMemberId);

        //2. 모든 알람을 삭제함 - 해당 회원의 크루 알람, 게임 알람
        crewAlarmService.deleteAllCrewAlarms();
        gameAlarmService.deleteAllGameAlarms();
    }

    private Member findMemberById(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND, memberId));
    }
}
