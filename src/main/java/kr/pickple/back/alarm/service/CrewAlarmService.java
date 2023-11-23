package kr.pickple.back.alarm.service;

import kr.pickple.back.alarm.domain.CrewAlarm;
import kr.pickple.back.alarm.dto.request.CrewAlarmUpdateStatusRequest;
import kr.pickple.back.alarm.dto.response.CrewAlarmResponse;
import kr.pickple.back.alarm.event.crew.CrewJoinRequestNotificationEvent;
import kr.pickple.back.alarm.event.crew.CrewMemberJoinedEvent;
import kr.pickple.back.alarm.event.crew.CrewMemberRejectedEvent;
import kr.pickple.back.alarm.exception.AlarmException;
import kr.pickple.back.alarm.repository.CrewAlarmRepository;
import kr.pickple.back.alarm.util.SseEmitters;
import kr.pickple.back.common.domain.RegistrationStatus;
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
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

import static kr.pickple.back.alarm.domain.AlarmStatus.FALSE;
import static kr.pickple.back.alarm.domain.CrewAlarmType.*;
import static kr.pickple.back.alarm.exception.AlarmExceptionCode.ALARM_NOT_FOUND;
import static kr.pickple.back.common.domain.RegistrationStatus.WAITING;
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
    private final SseEmitters sseEmitters;

    public CrewAlarmResponse createCrewJoinAlarm(final CrewJoinRequestNotificationEvent crewJoinRequestNotificationEvent) {

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

        sseEmitters.notify(leader.getId(), response);
        return response;
    }

    public CrewAlarmResponse createCrewMemberApproveAlarm(final CrewMemberJoinedEvent crewMemberJoinedEvent) {

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

        sseEmitters.notify(member.getId(), response);
        return response;
    }

    public CrewAlarmResponse createCrewMemberDeniedAlarm(final CrewMemberRejectedEvent crewMemberRejectedEvent) {

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

        sseEmitters.notify(member.getId(), response);
        return response;
    }

    private Crew getCrewInfo(final Long crewId) {
        final Crew crew = crewRepository.findById(crewId)
                .orElseThrow(() -> new CrewException(CREW_NOT_FOUND, crewId));

        return crew;
    }

    private Member getCrewLeaderOfCrew(final Long crewId) {
        final Crew crew = getCrewInfo(crewId);
        return crew.getLeader();
    }

    private List<Member> getCrewMembers(final Long crewId, final RegistrationStatus status) {
        final Crew crew = getCrewInfo(crewId);
        return crew.getCrewMembers(status);
    }

    private Member getMemberInfo(final Long memberId) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND, memberId));
        return member;
    }

    private void validateIsLeader(final CrewJoinRequestNotificationEvent crewJoinRequestNotificationEvent) {
        final Long crewId = crewJoinRequestNotificationEvent.getCrewId();
        final Crew crew = crewRepository.findById(crewId).orElseThrow(() -> new CrewException(CREW_NOT_FOUND, crewId));

        if (!crew.isLeader(crewJoinRequestNotificationEvent.getCrewLeader().getId())) {
            throw new CrewException(CREW_IS_NOT_LEADER, crewId, crew.getLeader());
        }
    }

    public void emitMessage(final CrewAlarmResponse crewAlarm) {

        final Long crewId = crewAlarm.getCrewId();
        final Member crewLeader = getCrewLeaderOfCrew(crewId);
        final List<Member> crewApplyMembers = getCrewMembers(crewId, WAITING);

        sendAlarmToCrewLeader(crewLeader, crewAlarm);
        sendAlarmToCrewApplyMembers(crewApplyMembers, crewAlarm);
    }

    private void sendAlarmToMember(final Member member, final CrewAlarmResponse crewAlarm) {
        final SseEmitter crewLeaderEmitter = sseEmitters.get(member.getId());
        if (crewLeaderEmitter != null) {
            try {
                crewLeaderEmitter.send(crewAlarm);
            } catch (IOException e) {
                sseEmitters.remove(member.getId());
                log.error("해당 회원에게 알람 전송 중 오류가 발생했습니다. : " + member.getId(), e);
            }
        }
    }

    private void sendAlarmToCrewLeader(final Member leader, final CrewAlarmResponse crewAlarm) {
        sendAlarmToMember(leader, crewAlarm);
    }

    private void sendAlarmToCrewApplyMembers(final List<Member> members, final CrewAlarmResponse crewAlarm) {
        for (final Member member : members) {
            sendAlarmToMember(member, crewAlarm);
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
