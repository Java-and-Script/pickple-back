package kr.pickple.back.alarm.service;

import kr.pickple.back.alarm.domain.AlarmStatus;
import kr.pickple.back.alarm.domain.CrewAlarm;
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
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

import static kr.pickple.back.alarm.domain.AlarmStatus.FALSE;
import static kr.pickple.back.alarm.domain.AlarmType.*;
import static kr.pickple.back.alarm.exception.AlarmExceptionCode.ALARM_NOT_FOUND;
import static kr.pickple.back.common.domain.RegistrationStatus.WAITING;
import static kr.pickple.back.crew.exception.CrewExceptionCode.CREW_IS_NOT_LEADER;
import static kr.pickple.back.crew.exception.CrewExceptionCode.CREW_NOT_FOUND;
import static kr.pickple.back.member.exception.MemberExceptionCode.MEMBER_NOT_FOUND;

//SSE 연결 로직에서는 @Transcational금지
@Slf4j
@Service
@RequiredArgsConstructor
public class CrewAlarmService {

    private final MemberRepository memberRepository;
    private final CrewRepository crewRepository;
    private final CrewAlarmRepository crewAlarmRepository;
    private final AlarmService alarmService;
    private final SseEmitters sseEmitters;

    //크루 알림 생성
    public CrewAlarmResponse createCrewJoinAlarm(final CrewJoinRequestNotificationEvent crewJoinRequestNotificationEvent) {
        //1.크루 리포지토리에서 해당 크루의 리더인지 확인
        validateIsLeader(crewJoinRequestNotificationEvent);

        //2.이벤트로 부터 크루 정보 가져오기
        final Long crewId = crewJoinRequestNotificationEvent.getCrewId();
        final Crew crew = getCrewInfo(crewId);
        final Member leader = crew.getLeader();

        //3. 알람 생성
        final CrewAlarm crewAlarm = CrewAlarm.builder()
                .crew(crew)
                .member(leader)
                .alarmType(CREW_LEADER_WAITING)
                .build();

        //4. DB에다가 알람 저장
        crewAlarmRepository.save(crewAlarm);

        final CrewAlarmResponse response = CrewAlarmResponse.of(crewAlarm);
        alarmService.notify(leader.getId(), response);

        return response;
    }

    public CrewAlarmResponse createCrewMemberApproveAlarm(final CrewMemberJoinedEvent crewMemberJoinedEvent) {

        //1.이벤트로부터 크루 정보, 회원 정보 가져오기
        final Long crewId = crewMemberJoinedEvent.getCrewId();
        final Crew crew = getCrewInfo(crewId);
        final Long memberId = crewMemberJoinedEvent.getMemberId();
        final Member member = getMemberInfo(memberId);

        //3. 알람 생성
        final CrewAlarm crewAlarm = CrewAlarm.builder()
                .crew(crew)
                .member(member)
                .alarmType(CREW_ACCEPT)
                .build();

        //4. 알람 DB에다가 저장
        crewAlarmRepository.save(crewAlarm);

        final CrewAlarmResponse response = CrewAlarmResponse.of(crewAlarm);
        alarmService.notify(member.getId(), response);

        return response;
    }

    public CrewAlarmResponse createCrewMemberDeniedAlarm(final CrewMemberRejectedEvent crewMemberRejectedEvent) {

        //1.이벤트로 부터 크루 정보,회원 정보 가져오기
        final Long crewId = crewMemberRejectedEvent.getCrewId();
        final Crew crew = getCrewInfo(crewId);
        final Long memberId = crewMemberRejectedEvent.getMemberId();
        final Member member = getMemberInfo(memberId);

        //3. 알람 생성
        final CrewAlarm crewAlarm = CrewAlarm.builder()
                .crew(crew)
                .member(member)
                .alarmType(CREW_DENIED)
                .build();


        //4. 알람 DB에다가 저장
        crewAlarmRepository.save(crewAlarm);

        final CrewAlarmResponse response = CrewAlarmResponse.of(crewAlarm);
        alarmService.notify(member.getId(), response);

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

        //해당 크루의 id와 크루장의 id를 비교하여 해당 크루장이 맞는지 체크
        if (!crew.isLeader(crewJoinRequestNotificationEvent.getCrewId())) {
            throw new CrewException(CREW_IS_NOT_LEADER, crewId, crew.getLeader());
        }
    }


    //SSE알람을 발송하는 부분
    public void emitMessage(final CrewAlarmResponse crewAlarm) {

        final Long crewId = crewAlarm.getCrewId();
        final Member crewLeader = getCrewLeaderOfCrew(crewId);
        final List<Member> crewApplyMembers = getCrewMembers(crewId, WAITING);

        //1. SSE로 알람 생성 - 각 케이스 별 알람 생성(크루장과 지원자에게 메시지 전송)
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

    //크루장에게 가입 신청이 올 시 받는 알람
    private void sendAlarmToCrewLeader(final Member leader, final CrewAlarmResponse crewAlarm) {
        sendAlarmToMember(leader, crewAlarm);
    }

    //회원(크루원 - 대기)에게 크루장이 승락 시, 상태가 Confired로 변하며 승락되었다는 알람
    //회원(크루원 - 대기)에게 크루장이 거절 시, 크루원 테이블에서 삭제되며, 거절되었다는 알람
    private void sendAlarmToCrewApplyMembers(final List<Member> members, final CrewAlarmResponse crewAlarm) {
        for (final Member member : members) {
            sendAlarmToMember(member, crewAlarm);
        }
    }

    //크루 알람 모두 찾기 - 미정
    public void findCrewAlarmAll() {

    }

    //크루 알림에서 isRead가 False가 있는지 체크하는 메소드
    public boolean checkUnreadCrewAlarm(final Long memberId) {
        //1.해당 회원의 읽지 않은 알람이 있는지 체크함
        final boolean existsUnreadCrewAlarm = crewAlarmRepository.existsByMemberIdAndIsRead(memberId, FALSE);

        //2.반환
        return existsUnreadCrewAlarm;
    }

    //크루 알림 찾기 By ID
    public CrewAlarm findCrewAlarmById(final Long crewAlarmId) {
        //1. 알람 ID로 해당 알림 찾기
        final CrewAlarm crewAlarm = checkExistCrewAlarm(crewAlarmId);

        //2.찾는 알람 반환
        return crewAlarm;
    }

    //크루 알람 상태 변경
    public void updateCrewAlarmStatus(final Long crewAlarmId, final String isRead) {
        //1.알람 ID로 해당 알림 찾기
        final CrewAlarm crewAlarm = checkExistCrewAlarm(crewAlarmId);
        final AlarmStatus alarmStatus = AlarmStatus.from(isRead);

        //2.상태 업데이트
        crewAlarm.updateStatus(alarmStatus);

        //3.저장
        crewAlarmRepository.save(crewAlarm);
    }

    private CrewAlarm checkExistCrewAlarm(final Long crewAlarmId) {
        final CrewAlarm crewAlarm = crewAlarmRepository.findById(crewAlarmId)
                .orElseThrow(() -> new AlarmException(ALARM_NOT_FOUND, crewAlarmId));

        return crewAlarm;
    }

    //크루 알림 삭제
    public void deleteAllCrewAlarms() {
        //1.DB에서 생성된 모든 크루 알람을 삭제함
        crewAlarmRepository.deleteAll();
    }
}
