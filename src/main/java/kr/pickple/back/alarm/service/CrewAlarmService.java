package kr.pickple.back.alarm.service;

import static kr.pickple.back.alarm.domain.CrewAlarmType.*;
import static kr.pickple.back.alarm.exception.AlarmExceptionCode.*;
import static kr.pickple.back.crew.exception.CrewExceptionCode.*;
import static kr.pickple.back.member.exception.MemberExceptionCode.*;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.alarm.domain.CrewAlarm;
import kr.pickple.back.alarm.domain.CrewAlarmType;
import kr.pickple.back.alarm.dto.request.CrewAlarmUpdateStatusRequest;
import kr.pickple.back.alarm.dto.response.CrewAlarmResponse;
import kr.pickple.back.alarm.event.crew.CrewJoinRequestNotificationEvent;
import kr.pickple.back.alarm.event.crew.CrewMemberJoinedEvent;
import kr.pickple.back.alarm.event.crew.CrewMemberRejectedEvent;
import kr.pickple.back.alarm.exception.AlarmException;
import kr.pickple.back.alarm.repository.CrewAlarmRepository;
import kr.pickple.back.crew.exception.CrewException;
import kr.pickple.back.crew.repository.CrewRepository;
import kr.pickple.back.crew.repository.entity.CrewEntity;
import kr.pickple.back.member.exception.MemberException;
import kr.pickple.back.member.repository.MemberRepository;
import kr.pickple.back.member.repository.entity.MemberEntity;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CrewAlarmService {

    private final MemberRepository memberRepository;
    private final CrewRepository crewRepository;
    private final CrewAlarmRepository crewAlarmRepository;
    private final SseEmitterService sseEmitterService;

    @Transactional
    public void createCrewJoinAlarm(final CrewJoinRequestNotificationEvent crewJoinRequestNotificationEvent) {
        final Long crewId = crewJoinRequestNotificationEvent.getCrewId();
        final Long leaderId = crewJoinRequestNotificationEvent.getMemberId();
        final CrewEntity crew = getCrewById(crewId);

        if (!crew.isLeader(leaderId)) {
            throw new CrewException(CREW_IS_NOT_LEADER, crewId, leaderId);
        }

        createCrewMemberAlarm(crewId, leaderId, CREW_LEADER_WAITING);
    }

    @Transactional
    public void createCrewMemberApproveAlarm(final CrewMemberJoinedEvent crewMemberJoinedEvent) {
        final Long crewId = crewMemberJoinedEvent.getCrewId();
        final Long memberId = crewMemberJoinedEvent.getMemberId();

        createCrewMemberAlarm(crewId, memberId, CREW_ACCEPT);
    }

    @Transactional
    public void createCrewMemberDeniedAlarm(final CrewMemberRejectedEvent crewMemberRejectedEvent) {
        final Long crewId = crewMemberRejectedEvent.getCrewId();
        final Long memberId = crewMemberRejectedEvent.getMemberId();

        createCrewMemberAlarm(crewId, memberId, CREW_DENIED);
    }

    private void createCrewMemberAlarm(final Long crewId, final Long memberId, final CrewAlarmType crewAlarmType) {
        final CrewEntity crew = getCrewById(crewId);
        final MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND, memberId));

        final CrewAlarm crewAlarm = CrewAlarm.builder()
                .crew(crew)
                .member(member)
                .crewAlarmType(crewAlarmType)
                .build();

        crewAlarmRepository.save(crewAlarm);
        sseEmitterService.sendAlarm(memberId, CrewAlarmResponse.from(crewAlarm));
    }

    private CrewEntity getCrewById(final Long crewId) {
        return crewRepository.findById(crewId)
                .orElseThrow(() -> new CrewException(CREW_NOT_FOUND, crewId));
    }

    public List<CrewAlarmResponse> findByMemberId(
            final Long loggedInMemberId,
            final Optional<Long> optionalCursorId,
            final Integer size
    ) {
        final List<CrewAlarm> crewAlarms = optionalCursorId
                .map(cursorId -> crewAlarmRepository.findByMemberIdAndIdLessThanOrderByCreatedAtDesc(
                        loggedInMemberId,
                        cursorId,
                        PageRequest.of(0, size)
                ))
                .orElseGet(() -> crewAlarmRepository.findByMemberIdOrderByCreatedAtDesc(
                        loggedInMemberId,
                        PageRequest.of(0, size)
                ));

        return crewAlarms.stream()
                .map(CrewAlarmResponse::from)
                .toList();
    }

    public boolean checkUnreadCrewAlarm(final Long memberId) {
        return crewAlarmRepository.existsByMemberIdAndIsRead(memberId, false);
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
        final CrewAlarm crewAlarm = crewAlarmRepository.findByMemberIdAndId(loggedInMemberId, crewAlarmId)
                .orElseThrow(() -> new AlarmException(ALARM_NOT_FOUND, loggedInMemberId, crewAlarmId));

        crewAlarm.updateStatus(crewAlarmUpdateStatusRequest.getIsRead());
    }
}
