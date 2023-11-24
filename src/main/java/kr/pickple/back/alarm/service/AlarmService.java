package kr.pickple.back.alarm.service;

import kr.pickple.back.alarm.dto.response.AlarmExistStatusResponse;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.exception.MemberException;
import kr.pickple.back.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static kr.pickple.back.member.exception.MemberExceptionCode.MEMBER_NOT_FOUND;

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
