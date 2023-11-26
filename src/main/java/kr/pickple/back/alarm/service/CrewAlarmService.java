package kr.pickple.back.alarm.service;

import kr.pickple.back.alarm.domain.CrewAlarm;
import kr.pickple.back.alarm.dto.request.CrewAlarmUpdateStatusRequest;
import kr.pickple.back.alarm.dto.response.CrewAlarmResponse;
import kr.pickple.back.alarm.event.crew.CrewJoinRequestNotificationEvent;
import kr.pickple.back.alarm.event.crew.CrewMemberJoinedEvent;
import kr.pickple.back.alarm.event.crew.CrewMemberRejectedEvent;
import kr.pickple.back.alarm.exception.AlarmException;
import kr.pickple.back.alarm.repository.CrewAlarmRepository;
import kr.pickple.back.crew.domain.Crew;
import kr.pickple.back.crew.exception.CrewException;
import kr.pickple.back.crew.repository.CrewRepository;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.exception.MemberException;
import kr.pickple.back.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static kr.pickple.back.alarm.domain.AlarmStatus.FALSE;
import static kr.pickple.back.alarm.domain.CrewAlarmType.*;
import static kr.pickple.back.alarm.exception.AlarmExceptionCode.ALARM_NOT_FOUND;
import static kr.pickple.back.crew.exception.CrewExceptionCode.CREW_IS_NOT_LEADER;
import static kr.pickple.back.crew.exception.CrewExceptionCode.CREW_NOT_FOUND;
import static kr.pickple.back.member.exception.MemberExceptionCode.MEMBER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CrewAlarmService {

    private final MemberRepository memberRepository;
    private final CrewRepository crewRepository;
    private final CrewAlarmRepository crewAlarmRepository;
    private final SseEmitterService sseEmitterService;

    @Transactional
    public void createCrewJoinAlarm(final CrewJoinRequestNotificationEvent crewJoinRequestNotificationEvent) {

        validateIsLeader(crewJoinRequestNotificationEvent);

        final Long crewId = crewJoinRequestNotificationEvent.getCrewId();
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
    public void createCrewMemberApproveAlarm(final CrewMemberJoinedEvent crewMemberJoinedEvent) {
        final Long crewId = crewMemberJoinedEvent.getCrewId();
        final Crew crew = getCrewInfo(crewId);
        final Long memberId = crewMemberJoinedEvent.getMemberId();
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
    public void createCrewMemberDeniedAlarm(final CrewMemberRejectedEvent crewMemberRejectedEvent) {
        final Long crewId = crewMemberRejectedEvent.getCrewId();
        final Crew crew = getCrewInfo(crewId);
        final Long memberId = crewMemberRejectedEvent.getMemberId();
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

    private void validateIsLeader(final CrewJoinRequestNotificationEvent crewAlarmEvent) {
        final Long crewId = crewAlarmEvent.getCrewId();
        final Crew crew = crewRepository.findById(crewId).orElseThrow(() -> new CrewException(CREW_NOT_FOUND, crewId));

        if (!crew.isLeader(crewAlarmEvent.getMemberId())) {
            throw new CrewException(CREW_IS_NOT_LEADER, crewId, crew.getLeader());
        }
    }

    public List<CrewAlarmResponse> findByMemberId(final Long loggedInMemberId, final Long cursorId, final int size) {
        final List<CrewAlarm> crewAlarms;

        if (cursorId == null) {
            crewAlarms = crewAlarmRepository.findByMemberIdOrderByCreatedAtDesc(
                    loggedInMemberId,
                    PageRequest.of(0, size)
            );
        } else {
            crewAlarms = crewAlarmRepository.findByMemberIdAndIdLessThanOrderByCreatedAtDesc(
                    loggedInMemberId,
                    cursorId,
                    PageRequest.of(0, size)
            );
        }

        return crewAlarms.stream()
                .map(CrewAlarmResponse::from)
                .collect(Collectors.toList());
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
