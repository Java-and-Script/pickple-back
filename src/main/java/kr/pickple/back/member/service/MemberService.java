package kr.pickple.back.member.service;

import static kr.pickple.back.common.domain.RegistrationStatus.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.address.implement.AddressReader;
import kr.pickple.back.auth.domain.token.AuthTokens;
import kr.pickple.back.auth.implement.TokenManager;
import kr.pickple.back.crew.domain.Crew;
import kr.pickple.back.crew.repository.CrewMemberRepository;
import kr.pickple.back.crew.repository.CrewRepository;
import kr.pickple.back.member.domain.MemberProfile;
import kr.pickple.back.member.domain.NewMember;
import kr.pickple.back.member.implement.MemberReader;
import kr.pickple.back.member.implement.MemberWriter;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final AddressReader addressReader;
    private final TokenManager tokenManager;
    private final MemberWriter memberWriter;
    private final MemberReader memberReader;
    private final CrewRepository crewRepository;
    private final CrewMemberRepository crewMemberRepository;

    /**
     * 사용자 회원가입 (카카오)
     */
    @Transactional
    public NewMember createMember(final NewMember newMember) {
        final NewMember savedNewMember = memberWriter.create(newMember);
        final AuthTokens authTokens = tokenManager.create(savedNewMember.getMemberId());
        savedNewMember.updateAuthTokens(authTokens);

        return savedNewMember;
    }

    /**
     * 사용자 프로필 조회
     */
    public MemberProfile findMemberProfileById(final Long memberId) {
        final MemberProfile memberProfile = memberReader.readProfileByMemberId(memberId);

        final List<Crew> crews = crewMemberRepository.findAllByMemberIdAndStatus(memberProfile.getMemberId(), CONFIRMED)
                .stream()
                .map(crewMember -> crewRepository.getCrewById(crewMember.getCrewId()))
                .toList();

        memberProfile.updateJoinedCrews(crews);

/*      final List<CrewResponse> crewResponses = crews.stream()
                .map(crew -> CrewResponse.of(
                                crew,
                                getLeaderResponse(crew),
                                addressReader.readMainAddressById(crew.getAddressDepth1Id(), crew.getAddressDepth2Id())
                        )
                )
                .toList();
                */

        return memberProfile;
    }

    /*private MemberResponse getLeaderResponse(final Crew crew) {
        final Long memberId = crew.getLeaderId();
        final Member member = memberReader.readByMemberId(memberId);

        final MainAddress mainAddress = addressReader.readMainAddressById(
                member.getAddressDepth1Id(),
                member.getAddressDepth2Id()
        );

        return MemberResponse.of(member, getPositionsByMember(member), mainAddress);
    }

    private List<Position> getPositionsByMember(final Member member) {
        final List<MemberPosition> memberPositions = memberPositionReader.readAll(member.getId());

        return Position.fromMemberPositions(memberPositions);
    }*/
}
