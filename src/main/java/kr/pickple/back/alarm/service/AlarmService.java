package kr.pickple.back.alarm.service;

import static kr.pickple.back.member.exception.MemberExceptionCode.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import kr.pickple.back.alarm.dto.response.AlarmExistStatusResponse;
import kr.pickple.back.alarm.dto.response.AlarmResponse;
import kr.pickple.back.alarm.dto.response.CrewAlarmResponse;
import kr.pickple.back.alarm.dto.response.GameAlarmResponse;
import kr.pickple.back.alarm.util.CursorResult;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.exception.MemberException;
import kr.pickple.back.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final GameAlarmService gameAlarmService;
    private final CrewAlarmService crewAlarmService;
    private final MemberRepository memberRepository;
    private final SseEmitterService sseEmitterService;

    public SseEmitter subscribeToSse(final Long loggedInMemberId) {
        return sseEmitterService.subscribeToSse(loggedInMemberId);
    }

    public CursorResult<AlarmResponse> findAllAlarms(final Long loggedInMemberId, final Long cursorId,
            final Integer size) {
        final List<AlarmResponse> alarms = new ArrayList<>();
        final Integer checkSizeForNextPage = size + 1;

        final List<CrewAlarmResponse> crewAlarms = crewAlarmService.findByMemberId(loggedInMemberId,
                Optional.ofNullable(cursorId), checkSizeForNextPage);
        final List<GameAlarmResponse> gameAlarms = gameAlarmService.findByMemberId(loggedInMemberId,
                Optional.ofNullable(cursorId), checkSizeForNextPage);

        alarms.addAll(crewAlarms);
        alarms.addAll(gameAlarms);
        alarms.sort(Comparator.comparing(AlarmResponse::getCreatedAt).reversed());

        Boolean hasNext = alarms.size() > size;
        Long nextCursorId = null;

        if (hasNext) {
            AlarmResponse lastAlarm = alarms.remove(size.intValue());
            nextCursorId = lastAlarm.getAlarmId();
            hasNext = alarms.size() > size;
        }

        return CursorResult.<AlarmResponse>builder()
                .alarmResponse(alarms)
                .cursorId(nextCursorId)
                .hasNext(hasNext)
                .build();
    }

    @Transactional
    public AlarmExistStatusResponse checkUnReadAlarms(final Long loggedInMemberId) {
        final boolean existsUnreadCrewAlarm = crewAlarmService.checkUnreadCrewAlarm(loggedInMemberId);
        final boolean existsUnreadGameAlarm = gameAlarmService.checkUnreadGameAlarm(loggedInMemberId);
        final boolean unreadAlarmExist = existsUnreadCrewAlarm || existsUnreadGameAlarm;

        return AlarmExistStatusResponse.builder()
                .unread(unreadAlarmExist)
                .build();
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
