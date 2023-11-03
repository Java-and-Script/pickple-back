package kr.pickple.back.crew.service;

import kr.pickple.back.common.domain.RegistrationStatus;
import kr.pickple.back.crew.domain.Crew;
import kr.pickple.back.crew.domain.CrewMember;
import kr.pickple.back.crew.dto.request.CrewApplyRequest;
import kr.pickple.back.crew.exception.CrewException;
import kr.pickple.back.crew.repository.CrewMemberRepository;
import kr.pickple.back.crew.repository.CrewRepository;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.exception.MemberException;
import kr.pickple.back.member.repository.MemberRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static kr.pickple.back.crew.exception.CrewExceptionCode.CREW_MEMBER_ALREADY_JOINED;
import static kr.pickple.back.crew.exception.CrewExceptionCode.CREW_NOT_FOUND;
import static kr.pickple.back.member.exception.MemberExceptionCode.MEMBER_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class CrewMemberService {

    private final CrewRepository crewRepository;
    private final MemberRepository memberRepository;
    private final CrewMemberRepository crewMemberRepository;

    @Transactional
    public void applyForCrewMemberShip(final Long crewId, final CrewApplyRequest crewApplyRequest) {
        final Crew crew = findByExistCrew(crewId);
        final Member member = findByExistMember(crewApplyRequest.getMemberId());

        findByIsConfirmCrewMember(member, crew);

        final CrewMember crewMember = CrewMember.builder()
                .crew(crew)
                .member(member)
                .status(RegistrationStatus.WAITING)
                .build();
        crewMemberRepository.save(crewMember);
    }

    private Crew findByExistCrew(final Long crewId) {
        return crewRepository.findById(crewId)
                .orElseThrow(() -> new CrewException(CREW_NOT_FOUND, crewId));
    }

    private void findByIsConfirmCrewMember(final Member member, final Crew crew) {
        Optional<CrewMember> crewMember = crewMemberRepository.findByMemberAndCrew(member, crew);
        if (crewMember.isPresent() && crewMember.get().getStatus() == RegistrationStatus.CONFIRMED) {
            throw new CrewException(CREW_MEMBER_ALREADY_JOINED, member.getId());
        }
    }

    private Member findByExistMember(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND, memberId));
    }
}