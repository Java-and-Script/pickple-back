package kr.pickple.back.alarm.service;

import kr.pickple.back.alarm.domain.AlarmExistsStatus;
import kr.pickple.back.alarm.util.SseEmitters;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.exception.MemberException;
import kr.pickple.back.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

import static kr.pickple.back.alarm.domain.AlarmExistsStatus.EXISTS;
import static kr.pickple.back.alarm.domain.AlarmExistsStatus.NOT_EXISTS;
import static kr.pickple.back.member.exception.MemberExceptionCode.MEMBER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final GameAlarmService gameAlarmService;
    private final CrewAlarmService crewAlarmService;
    private final MemberRepository memberRepository;
    private final SseEmitters sseEmitters;

    public SseEmitter subscribeToSse(final Long loggedInMemberId) {
        final Member member = findMemberById(loggedInMemberId);
        final SseEmitter emitter = new SseEmitter();

        try {
            emitter.send(SseEmitter.event()
                    .name("AlarmSseConnect")
                    .data("사용자에 대한 알람 SSE 연결이 정상적으로 처리되었습니다."));
        } catch (IOException e) {
            sseEmitters.remove(loggedInMemberId);
            emitter.completeWithError(e);
        }

        return emitter;
    }

    @Transactional
    public AlarmExistsStatus checkUnReadAlarms(final Long loggedInMemberId) {
        final boolean existsUnreadCrewAlarm = crewAlarmService.checkUnreadCrewAlarm(loggedInMemberId);
        final boolean existsUnreadGameAlarm = gameAlarmService.checkUnreadGameAlarm(loggedInMemberId);

        return existsUnreadCrewAlarm || existsUnreadGameAlarm ? EXISTS : NOT_EXISTS;
    }

    @Transactional
    public void deleteAllAlarms(final Long loggedInMemberId) {
        final Member member = findMemberById(loggedInMemberId);

        crewAlarmService.deleteAllCrewAlarms(member.getId());
        gameAlarmService.deleteAllGameAlarms(member.getId());
    }

    private Member findMemberById(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND, memberId));
    }
}
