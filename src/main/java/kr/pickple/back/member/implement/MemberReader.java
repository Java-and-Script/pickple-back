package kr.pickple.back.member.implement;

import static kr.pickple.back.common.domain.RegistrationStatus.*;
import static kr.pickple.back.crew.exception.CrewExceptionCode.*;
import static kr.pickple.back.member.exception.MemberExceptionCode.*;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.address.dto.response.MainAddress;
import kr.pickple.back.address.implement.AddressReader;
import kr.pickple.back.chat.domain.ChatRoom;
import kr.pickple.back.chat.repository.ChatRoomRepository;
import kr.pickple.back.crew.domain.Crew;
import kr.pickple.back.crew.exception.CrewException;
import kr.pickple.back.crew.implement.CrewMapper;
import kr.pickple.back.crew.repository.CrewMemberRepository;
import kr.pickple.back.crew.repository.CrewRepository;
import kr.pickple.back.crew.repository.entity.CrewEntity;
import kr.pickple.back.crew.repository.entity.CrewMemberEntity;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.domain.MemberDomain;
import kr.pickple.back.member.domain.MemberPosition;
import kr.pickple.back.member.domain.MemberProfile;
import kr.pickple.back.member.exception.MemberException;
import kr.pickple.back.member.repository.MemberPositionRepository;
import kr.pickple.back.member.repository.MemberRepository;
import kr.pickple.back.position.domain.Position;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberReader {

    private final AddressReader addressReader;
    private final CrewRepository crewRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final MemberRepository memberRepository;
    private final MemberPositionRepository memberPositionRepository;
    private final ChatRoomRepository chatRoomRepository;

    public MemberDomain readByMemberId(final Long memberId) {
        final Member memberEntity = readEntityByMemberId(memberId);

        final MainAddress mainAddress = addressReader.readMainAddressById(
                memberEntity.getAddressDepth1Id(),
                memberEntity.getAddressDepth2Id()
        );

        final List<Position> positions = memberPositionRepository.findAllByMemberId(memberId)
                .stream()
                .map(MemberPosition::getPosition)
                .toList();

        return MemberMapper.mapToMemberDomain(memberEntity, mainAddress, positions);
    }

    public MemberProfile readProfileByMemberId(final Long memberId) {
        final Member member = readEntityByMemberId(memberId);
        final MainAddress mainAddress = addressReader.readMainAddressById(
                member.getAddressDepth1Id(),
                member.getAddressDepth2Id()
        );
        final List<Position> positions = readPositionsByMemberId(memberId);

        final List<Crew> crews = readCrewsByMemberId(member.getId());

        return MemberMapper.mapToMemberProfileDomain(member, mainAddress, positions, crews);
    }

    private List<Crew> readCrewsByMemberId(final Long memberId) {
        return crewMemberRepository.findAllByMemberIdAndStatus(memberId, CONFIRMED)
                .stream()
                .map(this::readCrewEntityByCrewId)
                .map(crewEntity -> {
                    final MainAddress mainAddress = addressReader.readMainAddressById(
                            crewEntity.getAddressDepth1Id(),
                            crewEntity.getAddressDepth2Id()
                    );
                    final MemberDomain leader = readMemberById(crewEntity.getLeaderId());
                    final ChatRoom chatRoom = chatRoomRepository.getChatRoomById(crewEntity.getChatRoomId());

                    return CrewMapper.mapCrewEntityToDomain(crewEntity, mainAddress, leader, chatRoom);
                })
                .toList();
    }

    private MemberDomain readMemberById(final Long memberId) {
        final Member memberEntity = readEntityByMemberId(memberId);

        final MainAddress mainAddress = addressReader.readMainAddressById(
                memberEntity.getAddressDepth1Id(),
                memberEntity.getAddressDepth2Id()
        );

        final List<Position> positions = memberPositionRepository.findAllByMemberId(memberId)
                .stream()
                .map(MemberPosition::getPosition)
                .toList();

        return MemberMapper.mapToMemberDomain(memberEntity, mainAddress, positions);
    }

    private CrewEntity readCrewEntityByCrewId(final CrewMemberEntity crewMember) {
        return crewRepository.findById(crewMember.getCrewId())
                .orElseThrow(() -> new CrewException(CREW_NOT_FOUND, crewMember.getCrewId()));
    }

    private List<Position> readPositionsByMemberId(final Long memberId) {
        return memberPositionRepository.findAllByMemberId(memberId)
                .stream()
                .map(MemberPosition::getPosition)
                .toList();
    }

    public Optional<Member> readByOauthId(final Long oauthId) {
        return memberRepository.findByOauthId(oauthId);
    }

    private Member readEntityByMemberId(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND, memberId));
    }
}
