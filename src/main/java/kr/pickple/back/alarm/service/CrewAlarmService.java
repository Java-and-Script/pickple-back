package kr.pickple.back.alarm.service;

import kr.pickple.back.alarm.domain.CrewAlarm;
import kr.pickple.back.alarm.dto.request.CrewAlarmUpdateStatusRequest;
import kr.pickple.back.alarm.dto.response.CrewAlarmResponse;
import kr.pickple.back.alarm.event.crew.CrewAlarmEvent;
import kr.pickple.back.alarm.exception.AlarmException;
import kr.pickple.back.alarm.repository.CrewAlarmRepository;
import kr.pickple.back.crew.domain.Crew;
import kr.pickple.back.crew.exception.CrewException;
import kr.pickple.back.crew.repository.CrewRepository;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.exception.MemberException;
import kr.pickple.back.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static kr.pickple.back.alarm.domain.AlarmStatus.FALSE;
import static kr.pickple.back.alarm.domain.CrewAlarmType.*;
import static kr.pickple.back.alarm.exception.AlarmExceptionCode.ALARM_NOT_FOUND;
import static kr.pickple.back.crew.exception.CrewExceptionCode.CREW_IS_NOT_LEADER;
import static kr.pickple.back.crew.exception.CrewExceptionCode.CREW_NOT_FOUND;
import static kr.pickple.back.member.exception.MemberExceptionCode.MEMBER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrewAlarmService {

    private final MemberRepository memberRepository;
    private final CrewRepository crewRepository;
    private final CrewAlarmRepository crewAlarmRepository;
    private final SseEmitterService sseEmitterService;

    @Transactional
    public void createCrewJoinAlarm(final CrewAlarmEvent crewAlarmEvent) {

        validateIsLeader(crewAlarmEvent);

        final Long crewId = crewAlarmEvent.getCrewId();
        final Crew crew = getCrewInfo(crewId);
        final Member leader = crew.getLeader();

        final CrewAlarm crewAlarm = CrewAlarm.builder()
                .crew(crew)
                .member(leader)
                .crewAlarmType(CREW_LEADER_WAITING)
                .build();

        crewAlarmRepository.save(crewAlarm);

        final CrewAlarmResponse response = CrewAlarmResponse.from(crewAlarm);
        sseEmitterService.sendAlarm(leader.getId(), response);
    }

    @Transactional
    public void createCrewMemberApproveAlarm(final CrewAlarmEvent crewAlarmEvent) {
        final Long crewId = crewAlarmEvent.getCrewId();
        final Crew crew = getCrewInfo(crewId);
        final Long memberId = crewAlarmEvent.getMemberId();
        final Member member = getMemberInfo(memberId);

        final CrewAlarm crewAlarm = CrewAlarm.builder()
                .crew(crew)
                .member(member)
                .crewAlarmType(CREW_ACCEPT)
                .build();

        crewAlarmRepository.save(crewAlarm);

        final CrewAlarmResponse response = CrewAlarmResponse.from(crewAlarm);
        sseEmitterService.sendAlarm(member.getId(), response);
    }

    @Transactional
    public void createCrewMemberDeniedAlarm(final CrewAlarmEvent crewAlarmEvent) {
        final Long crewId = crewAlarmEvent.getCrewId();
        final Crew crew = getCrewInfo(crewId);
        final Long memberId = crewAlarmEvent.getMemberId();
        final Member member = getMemberInfo(memberId);

        final CrewAlarm crewAlarm = CrewAlarm.builder()
                .crew(crew)
                .member(member)
                .crewAlarmType(CREW_DENIED)
                .build();

        crewAlarmRepository.save(crewAlarm);

        final CrewAlarmResponse response = CrewAlarmResponse.from(crewAlarm);
        sseEmitterService.sendAlarm(member.getId(), response);
    }

    private Crew getCrewInfo(final Long crewId) {
        final Crew crew = crewRepository.findById(crewId)
                .orElseThrow(() -> new CrewException(CREW_NOT_FOUND, crewId));

        return crew;
    }

    private Member getMemberInfo(final Long memberId) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND, memberId));
        return member;
    }

    private void validateIsLeader(final CrewAlarmEvent crewAlarmEvent) {
        final Long crewId = crewAlarmEvent.getCrewId();
        final Crew crew = crewRepository.findById(crewId).orElseThrow(() -> new CrewException(CREW_NOT_FOUND, crewId));

        if (!crew.isLeader(crewAlarmEvent.getMemberId())) {
            throw new CrewException(CREW_IS_NOT_LEADER, crewId, crew.getLeader());
        }
    }

    public boolean checkUnreadCrewAlarm(final Long memberId) {
        final boolean existsUnreadCrewAlarm = crewAlarmRepository.existsByMemberIdAndIsRead(memberId, FALSE);

        return existsUnreadCrewAlarm;
    }

    @Transactional
    public void deleteAllCrewAlarms(final Long memberId) {
        crewAlarmRepository.deleteByMemberId(memberId);
    }

    @Transactional
    public void updateCrewAlarmById(
            final Long loggedInMemberId,
            final Long crewAlarmId,
            final CrewAlarmUpdateStatusRequest crewAlarmUpdateStatusRequest
    ) {
        final Member member = findMemberById(loggedInMemberId);
        final CrewAlarm crewAlarm = checkExistCrewAlarm(loggedInMemberId, crewAlarmId);

        crewAlarm.updateStatus(crewAlarmUpdateStatusRequest.getIsRead());
    }

    private Member findMemberById(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND, memberId));
    }

    public CrewAlarm checkExistCrewAlarm(final Long memberId, final Long crewAlarmId) {
        return crewAlarmRepository.findByMemberIdAndId(memberId, crewAlarmId)
                .orElseThrow(() -> new AlarmException(ALARM_NOT_FOUND, memberId, crewAlarmId));
    }
}
