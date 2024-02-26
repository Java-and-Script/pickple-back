package kr.pickple.back.crew.service;

import static kr.pickple.back.common.domain.RegistrationStatus.*;
import static kr.pickple.back.crew.exception.CrewExceptionCode.*;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.address.implement.AddressReader;
import kr.pickple.back.alarm.event.crew.CrewJoinRequestNotificationEvent;
import kr.pickple.back.alarm.event.crew.CrewMemberJoinedEvent;
import kr.pickple.back.alarm.event.crew.CrewMemberRejectedEvent;
import kr.pickple.back.chat.repository.ChatRoomRepository;
import kr.pickple.back.chat.service.ChatMessageService;
import kr.pickple.back.common.domain.RegistrationStatus;
import kr.pickple.back.crew.domain.Crew;
import kr.pickple.back.crew.domain.CrewMember;
import kr.pickple.back.crew.exception.CrewException;
import kr.pickple.back.crew.implement.CrewReader;
import kr.pickple.back.crew.implement.CrewWriter;
import kr.pickple.back.crew.repository.CrewMemberRepository;
import kr.pickple.back.crew.repository.CrewRepository;
import kr.pickple.back.crew.repository.entity.CrewEntity;
import kr.pickple.back.crew.repository.entity.CrewMemberEntity;
import kr.pickple.back.member.domain.MemberDomain;
import kr.pickple.back.member.implement.MemberReader;
import kr.pickple.back.member.repository.MemberPositionRepository;
import kr.pickple.back.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CrewMemberService {

    private final AddressReader addressReader;
    private final MemberReader memberReader;
    private final CrewReader crewReader;
    private final CrewWriter crewWriter;
    private final MemberRepository memberRepository;
    private final CrewRepository crewRepository;
    private final MemberPositionRepository memberPositionRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final ChatRoomRepository chatRoomRepository;

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
    public Crew findCrewMembersByStatus(
            final Long loggedInMemberId,
            final Long crewId,
            final RegistrationStatus status
    ) {
        final Crew crew = crewReader.read(crewId, status);

        if (!crew.isLeader(loggedInMemberId)) {
            throw new CrewException(CREW_IS_NOT_LEADER, loggedInMemberId);
        }

        return crew;
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

        crewWriter.updateRegistrationStatus(crewMember, newRegistrationStatus);
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
        final CrewMemberEntity crewMember = crewMemberRepository.getCrewMemberByCrewIdAndMemberId(memberId, crewId);
        final CrewEntity crew = crewRepository.getCrewById(crewId);

        if (crew.isLeader(loggedInMemberId)) {
            validateIsLeaderSelfDeleted(loggedInMemberId, memberId);

            eventPublisher.publishEvent(CrewMemberRejectedEvent.builder()
                    .crewId(crewId)
                    .memberId(memberId)
                    .build());

            deleteCrewMember(crewMember);

            return;
        }

        if (loggedInMemberId.equals(memberId)) {
            cancelCrewMember(crewMember);
            return;
        }

        throw new CrewException(CREW_MEMBER_NOT_ALLOWED, loggedInMemberId);
    }

    private void validateIsLeaderSelfDeleted(Long loggedInMemberId, Long memberId) {
        if (loggedInMemberId.equals(memberId)) {
            throw new CrewException(CREW_LEADER_CANNOT_BE_DELETED, loggedInMemberId);
        }
    }

    private void cancelCrewMember(final CrewMemberEntity crewMember) {
        if (crewMember.getStatus() != WAITING) {
            throw new CrewException(CREW_MEMBER_STATUS_IS_NOT_WAITING);
        }

        deleteCrewMember(crewMember);
    }

    private void deleteCrewMember(final CrewMemberEntity crewMember) {
        crewMemberRepository.delete(crewMember);
    }
}
