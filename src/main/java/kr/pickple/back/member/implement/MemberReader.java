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
import kr.pickple.back.crew.domain.Crew;
import kr.pickple.back.crew.exception.CrewException;
import kr.pickple.back.crew.implement.CrewMapper;
import kr.pickple.back.crew.repository.CrewMemberRepository;
import kr.pickple.back.crew.repository.CrewRepository;
import kr.pickple.back.crew.repository.entity.CrewEntity;
import kr.pickple.back.crew.repository.entity.CrewMemberEntity;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.domain.MemberProfile;
import kr.pickple.back.member.exception.MemberException;
import kr.pickple.back.member.repository.MemberPositionRepository;
import kr.pickple.back.member.repository.MemberRepository;
import kr.pickple.back.member.repository.entity.MemberEntity;
import kr.pickple.back.member.repository.entity.MemberPositionEntity;
import kr.pickple.back.position.domain.Position;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberReader {

    private final AddressReader addressReader;
    private final MemberRepository memberRepository;
    private final MemberPositionRepository memberPositionRepository;
    private final CrewRepository crewRepository;
    private final CrewMemberRepository crewMemberRepository;

    public Boolean existsByMemberId(final Long memberId) {
        return memberRepository.existsById(memberId);
    }

    public Optional<MemberEntity> readByOauthId(final Long oauthId) {
        return memberRepository.findByOauthId(oauthId);
    }

    public Member readByMemberId(final Long memberId) {
        final MemberEntity memberEntity = readEntityByMemberId(memberId);

        final MainAddress mainAddress = addressReader.readMainAddressById(
                memberEntity.getAddressDepth1Id(),
                memberEntity.getAddressDepth2Id()
        );

        final List<Position> positions = memberPositionRepository.findAllByMemberId(memberId)
                .stream()
                .map(MemberPositionEntity::getPosition)
                .toList();

        return MemberMapper.mapToMemberDomain(memberEntity, mainAddress, positions);
    }

    public MemberProfile readProfileByMemberId(final Long memberId) {
        final MemberEntity member = readEntityByMemberId(memberId);
        final MainAddress mainAddress = addressReader.readMainAddressById(
                member.getAddressDepth1Id(),
                member.getAddressDepth2Id()
        );
        final List<Position> positions = readPositionsByMemberId(memberId);

        final List<Crew> crews = readCrewsByMemberId(member.getId());

        return MemberMapper.mapToMemberProfileDomain(member, mainAddress, positions, crews);
    }

    private MemberEntity readEntityByMemberId(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND, memberId));
    }

    private List<Position> readPositionsByMemberId(final Long memberId) {
        return memberPositionRepository.findAllByMemberId(memberId)
                .stream()
                .map(MemberPositionEntity::getPosition)
                .toList();
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
                    final Member leader = readByMemberId(crewEntity.getLeaderId());

                    return CrewMapper.mapCrewEntityToDomain(crewEntity, mainAddress, leader);
                })
                .toList();
    }

    private CrewEntity readCrewEntityByCrewId(final CrewMemberEntity crewMember) {
        return crewRepository.findById(crewMember.getCrewId())
                .orElseThrow(() -> new CrewException(CREW_NOT_FOUND, crewMember.getCrewId()));
    }
}
