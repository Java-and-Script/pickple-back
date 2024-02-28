package kr.pickple.back.crew.service;

import static kr.pickple.back.common.domain.RegistrationStatus.*;
import static kr.pickple.back.crew.exception.CrewExceptionCode.*;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.alarm.event.crew.CrewJoinRequestNotificationEvent;
import kr.pickple.back.alarm.event.crew.CrewMemberJoinedEvent;
import kr.pickple.back.alarm.event.crew.CrewMemberRejectedEvent;
import kr.pickple.back.chat.service.ChatMessageService;
import kr.pickple.back.common.domain.RegistrationStatus;
import kr.pickple.back.crew.domain.Crew;
import kr.pickple.back.crew.domain.CrewMember;
import kr.pickple.back.crew.dto.mapper.CrewResponseMapper;
import kr.pickple.back.crew.dto.response.CrewProfileResponse;
import kr.pickple.back.crew.exception.CrewException;
import kr.pickple.back.crew.implement.CrewReader;
import kr.pickple.back.crew.implement.CrewWriter;
import kr.pickple.back.member.domain.MemberDomain;
import kr.pickple.back.member.implement.MemberReader;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CrewMemberService {

    private final MemberReader memberReader;
    private final CrewReader crewReader;
    private final CrewWriter crewWriter;

    private final ChatMessageService chatMessageService;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 크루 가입 신청
     */
    @Transactional
    public void registerCrewMember(final Long crewId, final Long loggedInMemberId) {
        final Crew crew = crewReader.read(crewId, CONFIRMED);
        final MemberDomain member = memberReader.readByMemberId(loggedInMemberId);

        crewWriter.register(member, crew);

        eventPublisher.publishEvent(CrewJoinRequestNotificationEvent.builder()
                .crewId(crewId)
                .memberId(crew.getLeader().getMemberId())
                .build());
    }

    /**
     * 크루에 가입 신청된 혹은 확정된 사용자 정보 목록 조회
     */
    public CrewProfileResponse findCrewMembersByStatus(
            final Long loggedInMemberId,
            final Long crewId,
            final RegistrationStatus status
    ) {
        final Crew crew = crewReader.read(crewId, status);

        if (!crew.isLeader(loggedInMemberId)) {
            throw new CrewException(CREW_IS_NOT_LEADER, loggedInMemberId);
        }

        return CrewResponseMapper.mapToCrewProfileResponseDto(crew);
    }

    /**
     * 크루 가입 신청 수락
     */
    @Transactional
    public void updateCrewMemberRegistrationStatus(
            final Long loggedInMemberId,
            final Long crewId,
            final Long memberId,
            final RegistrationStatus newRegistrationStatus
    ) {
        final CrewMember crewMember = crewReader.readCrewMember(memberId, crewId);
        final Crew crew = crewMember.getCrew();

        if (!crew.isLeader(loggedInMemberId)) {
            throw new CrewException(CREW_IS_NOT_LEADER, loggedInMemberId);
        }

        crewWriter.updateMemberRegistrationStatus(crewMember, newRegistrationStatus);
        chatMessageService.enterRoomAndSaveEnteringMessages(crew.getChatRoom(), crewMember.getMember());

        eventPublisher.publishEvent(CrewMemberJoinedEvent.builder()
                .crewId(crewId)
                .memberId(memberId)
                .build());
    }

    /**
     * 크루원 가입 신청 거절/취소
     */
    @Transactional
    public void deleteCrewMember(final Long loggedInMemberId, final Long crewId, final Long memberId) {
        final CrewMember crewMember = crewReader.readCrewMember(memberId, crewId);
        final Crew crew = crewMember.getCrew();

        if (crew.isLeader(loggedInMemberId)) {
            validateLeaderDeleteSelf(loggedInMemberId, memberId);
            crewWriter.delete(crewMember);

            eventPublisher.publishEvent(CrewMemberRejectedEvent.builder()
                    .crewId(crewId)
                    .memberId(memberId)
                    .build());

            return;
        }

        if (loggedInMemberId.equals(memberId)) {
            crewWriter.cancel(crewMember);

            return;
        }

        throw new CrewException(CREW_MEMBER_NOT_ALLOWED, loggedInMemberId);
    }

    private void validateLeaderDeleteSelf(final Long leaderId, final Long deletingMemberId) {
        if (leaderId.equals(deletingMemberId)) {
            throw new CrewException(CREW_LEADER_CANNOT_BE_DELETED, deletingMemberId);
        }
    }
}
