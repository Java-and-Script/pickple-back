package kr.pickple.back.alaram.service;

import kr.pickple.back.alaram.domain.CrewAlarm;
import kr.pickple.back.alaram.dto.request.CrewAlarmStatusUpdateRequest;
import kr.pickple.back.alaram.dto.response.CrewAlaramResponse;
import kr.pickple.back.alaram.event.crew.CrewJoinRequestNotificationEvent;
import kr.pickple.back.alaram.event.crew.CrewMemberJoinedEvent;
import kr.pickple.back.alaram.event.crew.CrewMemberRejectedEvent;
import kr.pickple.back.alaram.exception.AlarmException;
import kr.pickple.back.alaram.repository.CrewAlarmRepository;
import kr.pickple.back.crew.domain.Crew;
import kr.pickple.back.crew.exception.CrewException;
import kr.pickple.back.crew.repository.CrewRepository;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.exception.MemberException;
import kr.pickple.back.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static kr.pickple.back.alaram.domain.AlarmStatus.FALSE;
import static kr.pickple.back.alaram.domain.AlarmType.*;
import static kr.pickple.back.alaram.exception.AlarmExceptionCode.ALARM_NOT_FOUND;
import static kr.pickple.back.crew.exception.CrewExceptionCode.CREW_IS_NOT_LEADER;
import static kr.pickple.back.crew.exception.CrewExceptionCode.CREW_NOT_FOUND;
import static kr.pickple.back.member.exception.MemberExceptionCode.MEMBER_NOT_FOUND;

//SSE 연결 로직에서는 @Transcational금지
@Service
@RequiredArgsConstructor
public class CrewAlarmService {

    private final MemberRepository memberRepository;
    private final CrewRepository crewRepository;
    private final CrewAlarmRepository crewAlarmRepository;

    //크루 알림 생성

    public CrewAlarm createCrewJoinAlaram(CrewJoinRequestNotificationEvent crewJoinRequestNotificationEvent) {
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

        //?.SSE로 발생된 알람 발송 - 미완성
        //emitMessage(crewAlaramResponse.getCrewAlarm());

        //?. 발생된 알림을 해당 크루장에게 발송 - notify()메소드 - 미완성

        return CrewAlaramResponse.of(crewAlarm).getCrewAlarm();
    }

    public CrewAlarm createCrewMemberApproveAlaram(CrewMemberJoinedEvent crewMemberJoinedEvent) {

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

        //?.SSE로 발생된 알람 저장 - DB 저장
        //emitMessage(crewAlaramResponse.getCrewAlarm());


        //?.발생된 알림을 해당 Member에게 발송 - notifiy() 메소드

        return CrewAlaramResponse.of(crewAlarm).getCrewAlarm();
    }

    public CrewAlarm createCrewMemberDeniedAlaram(CrewMemberRejectedEvent crewMemberRejectedEvent) {

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


        //?. SSE로 발생된 알람 저장 - DB 저장
        //emitMessage(crewAlaramResponse.getCrewAlarm());

        //?. 발생된 알림을 해당 Member에게 발송 - notify()메소드

        return CrewAlaramResponse.of(crewAlarm).getCrewAlarm();
    }

    private Crew getCrewInfo(Long crewId) {
        final Crew crew = crewRepository.findById(crewId)
                .orElseThrow(() -> new CrewException(CREW_NOT_FOUND, crewId));
        return crew;
    }

    private Member getMemberInfo(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND, memberId));
    }

    private void validateIsLeader(CrewJoinRequestNotificationEvent crewJoinRequestNotificationEvent) {
        final Long crewId = crewJoinRequestNotificationEvent.getCrewId();
        final Crew crew = crewRepository.findById(crewId).orElseThrow(() -> new CrewException(CREW_NOT_FOUND, crewId));

        //해당 크루의 id와 크루장의 id를 비교하여 해당 크루장이 맞는지 체크
        if (!crew.isLeader(crewJoinRequestNotificationEvent.getCrewId())) {
            throw new CrewException(CREW_IS_NOT_LEADER, crewId, crew.getLeader());
        }
    }


    //SSE알람을 발송하는 부분
    //1. 크루장에게 발송 (멤버(회원)이 크루에 지원 클릭 시)
    //2. 회원에게 발송(멤버(회훤) - 대기 상태 WAITING을 CONFIREMED로 변경 시)
    //3. 회원에게 발송(멤버(회원) - 해당 크루가 거절되었어요!)
    public void emitMessage(CrewAlarm alaram) {
        //1. SSE로 알람 생성 - 각 케이스 별 알람 생성

        //2. SSE로 발생된 알람 저장
    }

    //크루 알람 모두 찾기 - 미정
    public void findCrewAlaramAll() {

    }

    //크루 알림에서 isRead가 False가 있는지 체크하는 메소드
    public boolean checkUnreadCrewAlarm(final Long memberId){
        //1.해당 회원의 읽지 않은 알람이 있는지 체크함
        final boolean existsUnreadCrewAlarm = crewAlarmRepository.existsByMemberIdAndIsRead(memberId, FALSE);

        //2.반환
        return existsUnreadCrewAlarm;
    }



    //크루 알림 찾기 By ID
    public CrewAlarm findCrewAlramById(final Long crewAlarmId) {
        //1. 알람 ID로 해당 알림 찾기
        final CrewAlarm crewAlarm = checkExistCrewAlarm(crewAlarmId);

        //2.찾는 알람 반환
        return crewAlarm;
    }

    //크루 알람 상태 변경
    public void updateCrewAlaramStatus(final Long crewAlarmId, final CrewAlarmStatusUpdateRequest crewAlarmStatusUpdateRequest) {
        //1.알람 ID로 해당 알림 찾기
        final CrewAlarm crewAlarm = checkExistCrewAlarm(crewAlarmId);

        //2.상태 업데이트
        crewAlarm.updateStatus(crewAlarmStatusUpdateRequest.getIsRead());

        //3.저장
        crewAlarmRepository.save(crewAlarm);
    }

    private CrewAlarm checkExistCrewAlarm(final Long crewAlarmId) {
        final CrewAlarm crewAlarm = crewAlarmRepository.findById(crewAlarmId)
                .orElseThrow(() -> new AlarmException(ALARM_NOT_FOUND, crewAlarmId));

        return crewAlarm;
    }

    //크루 알림 삭제
    public void deleteAllCrewAlaram() {
        //1.DB에서 생성된 모든 크루 알람을 삭제함
        crewAlarmRepository.deleteAll();
    }
}
