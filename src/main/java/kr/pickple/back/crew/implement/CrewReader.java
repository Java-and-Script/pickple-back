package kr.pickple.back.crew.implement;

import static kr.pickple.back.crew.exception.CrewExceptionCode.*;
import static kr.pickple.back.member.exception.MemberExceptionCode.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.address.dto.response.MainAddress;
import kr.pickple.back.address.implement.AddressReader;
import kr.pickple.back.chat.domain.ChatRoom;
import kr.pickple.back.chat.repository.ChatRoomRepository;
import kr.pickple.back.common.domain.RegistrationStatus;
import kr.pickple.back.crew.domain.Crew;
import kr.pickple.back.crew.domain.CrewMember;
import kr.pickple.back.crew.exception.CrewException;
import kr.pickple.back.crew.repository.CrewMemberRepository;
import kr.pickple.back.crew.repository.CrewRepository;
import kr.pickple.back.crew.repository.entity.CrewEntity;
import kr.pickple.back.crew.repository.entity.CrewMemberEntity;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.domain.MemberDomain;
import kr.pickple.back.member.domain.MemberPosition;
import kr.pickple.back.member.exception.MemberException;
import kr.pickple.back.member.implement.MemberMapper;
import kr.pickple.back.member.repository.MemberPositionRepository;
import kr.pickple.back.member.repository.MemberRepository;
import kr.pickple.back.position.domain.Position;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CrewReader {

    private final AddressReader addressReader;
    private final MemberRepository memberRepository;
    private final MemberPositionRepository memberPositionRepository;
    private final CrewRepository crewRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final ChatRoomRepository chatRoomRepository;

    public Integer countByLeaderId(final Long leaderId) {
        return crewRepository.countByLeaderId(leaderId);
    }

    public Crew read(final Long crewId) {
        final CrewEntity crewEntity = crewRepository.findById(crewId)
                .orElseThrow(() -> new CrewException(CREW_NOT_FOUND, crewId));

        final MainAddress mainAddress = addressReader.readMainAddressById(
                crewEntity.getAddressDepth1Id(),
                crewEntity.getAddressDepth2Id()
        );

        final MemberDomain leader = readMemberById(crewEntity.getLeaderId());
        final ChatRoom chatRoom = chatRoomRepository.getChatRoomById(crewEntity.getChatRoomId());

        return CrewMapper.mapCrewEntityToDomain(crewEntity, mainAddress, leader, chatRoom);
    }

    public List<Crew> readNearCrewsByAddress(
            final String addressDepth1Name,
            final String addressDepth2Name,
            final Pageable pageable
    ) {
        final MainAddress mainAddress = addressReader.readMainAddressByNames(addressDepth1Name, addressDepth2Name);
        final Page<CrewEntity> crewEntities = crewRepository.findByAddressDepth1IdAndAddressDepth2Id(
                mainAddress.getAddressDepth1().getId(),
                mainAddress.getAddressDepth2().getId(),
                pageable
        );

        return crewEntities.stream()
                .map(crewEntity -> {
                    final MemberDomain leader = readMemberById(crewEntity.getLeaderId());
                    final ChatRoom chatRoom = chatRoomRepository.getChatRoomById(crewEntity.getChatRoomId());

                    return CrewMapper.mapCrewEntityToDomain(crewEntity, mainAddress, leader, chatRoom);
                })
                .toList();
    }

    public CrewMember readCrewMember(final Long memberId, final Long crewId) {
        final CrewMemberEntity crewMemberEntity = crewMemberRepository.findByMemberIdAndCrewId(memberId, crewId)
                .orElseThrow(() -> new CrewException(CREW_MEMBER_NOT_FOUND, memberId, crewId));
        final MemberDomain member = readMemberById(memberId);
        final Crew crew = read(crewId);

        return CrewMapper.mapCrewMemberEntityToDomain(crewMemberEntity, member, crew);
    }

    public List<MemberDomain> readAllMembersInStatus(final Long crewId, final RegistrationStatus status) {
        return crewMemberRepository.findAllByCrewIdAndStatus(crewId, status)
                .stream()
                .map(crewMemberEntity -> readMemberById(crewMemberEntity.getMemberId()))
                .toList();
    }

    private MemberDomain readMemberById(final Long memberId) {
        final Member memberEntity = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND, memberId));

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
}
