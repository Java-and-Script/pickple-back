package kr.pickple.back.alarm.service;

import kr.pickple.back.alarm.domain.AlarmExistsStatus;
import kr.pickple.back.alarm.domain.CrewAlarm;
import kr.pickple.back.alarm.domain.GameAlarm;
import kr.pickple.back.alarm.dto.response.AlarmResponse;
import kr.pickple.back.alarm.dto.response.CrewAlarmResponse;
import kr.pickple.back.alarm.dto.response.GameAlarmResponse;
import kr.pickple.back.alarm.exception.AlarmException;
import kr.pickple.back.alarm.exception.AlarmExceptionCode;
import kr.pickple.back.alarm.repository.AlarmRepository;
import kr.pickple.back.alarm.util.CursorResult;
import kr.pickple.back.alarm.util.SseEmitters;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.exception.MemberException;
import kr.pickple.back.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static kr.pickple.back.alarm.domain.AlarmExistsStatus.EXISTS;
import static kr.pickple.back.alarm.domain.AlarmExistsStatus.NOT_EXISTS;
import static kr.pickple.back.member.exception.MemberExceptionCode.MEMBER_NOT_FOUND;

//SSE 연결 로직에서는 @Transcational금지
@Service
@RequiredArgsConstructor
public class AlarmService {

    private final GameAlarmService gameAlarmService;
    private final CrewAlarmService crewAlarmService;
    private final MemberRepository memberRepository;
    private final AlarmRepository alarmRepository;
    private final SseEmitters sseEmitters;


    //1.SSE 연결 - 이벤트가 발생 된 후, 30초동안 지속 연결
    public SseEmitter subscribeToSse(final Long loggedInMemberId) {
        final Member member = findMemberById(loggedInMemberId);
        final SseEmitter emitter = new SseEmitter();

        try {
            emitter.send(SseEmitter.event()
                    .name("AlarmSseConnect") // 해당 이벤트의 이름 지정
                    .data("사용자에 대한 알람 SSE 연결이 정상적으로 처리되었습니다.")); // 503 에러 방지를 위한 더미 데이터
        } catch (IOException e) {
            sseEmitters.remove(loggedInMemberId);
            emitter.completeWithError(e);
        }

        return emitter;
    }

    //1. 해당 사용자의 모든 알람 목록 조회 시 - 생성일 순
    //2. 두, 알림에 대해서 가장 빠른 생성일 순으로 조회
    public CursorResult<AlarmResponse> findAllAlarms(final Long loggedInMemberId, final Long cursorId, final Integer size) {
        final Member member = findMemberById(loggedInMemberId);

        Pageable page = PageRequest.of(0, size);

        //1.각 알림을 조회하고, Response DTO로 변환
        List<CrewAlarmResponse> crewAlarmResponses;
        List<GameAlarmResponse> gameAlarmResponses;

        //1-1.초기 페이징 처리
        if (cursorId == 0) {
            crewAlarmResponses = crewAlarmService.findCrewAlarmByMemberIdOrderByIdDesc(member.getId(), page);
            gameAlarmResponses = gameAlarmService.findGameAlarmByMemberIdOrderByIdDesc(member.getId(), page);
        } else {
            crewAlarmResponses = crewAlarmService.findCrewAlarmByMemberIdAndIdLessThanOrderByIdDesc(member.getId(), cursorId, page);
            gameAlarmResponses = gameAlarmService.findGameAlarmByMemberIdAndIdLessThanOrderByIdDesc(member.getId(), cursorId, page);
        }

        //2.서로 다른 알람을 하나의 리스트에 넣음
        List<AlarmResponse> alarmResponses = new ArrayList<>();
        alarmResponses.addAll(crewAlarmResponses);
        alarmResponses.addAll(gameAlarmResponses);

        //3.서로 다른 알람들을 생성일 순으로 정렬
        alarmResponses.sort(Comparator.comparing(AlarmResponse::getCreatedAt).reversed());

        //3-1.초기 알람 사이즈
        if (alarmResponses.size() > size) {
            alarmResponses = alarmResponses.subList(0, size);
        }

        final Long lastIdOfList = alarmResponses.isEmpty() ? null : alarmResponses.get(alarmResponses.size() - 1).getId();

        return CursorResult.<AlarmResponse>builder()
                .alarmResponses(alarmResponses)
                .hasNext(hasNext(member.getId(), lastIdOfList))
                .build();
    }

    //크루와 게임에 대한 다음 페이지가 있는지 확인
    private boolean hasNext(final Long memberId, final Long id) {
        return crewAlarmService.existsCrewAlarmByMemberIdAndIdLessThan(memberId, id) || gameAlarmService.existsGameAlarmByMemberIdAndIdLessThan(memberId, id);
    }

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
        final CrewAlarm crewAlarm = crewAlarmService.findCrewAlarmById(alarmId);

        //3.해당 알람이 어떤 알람인지 확인하고, 상태 수정
        if (gameAlarm != null) {
            gameAlarmService.updateGameAlarmStatus(alarmId, isRead);
        }

        if (crewAlarm != null) {
            crewAlarmService.updateCrewAlarmStatus(alarmId, isRead);
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
