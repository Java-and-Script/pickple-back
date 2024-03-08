package kr.pickple.back.crew.implement;

import static kr.pickple.back.chat.exception.ChatExceptionCode.*;
import static kr.pickple.back.common.domain.RegistrationStatus.*;
import static kr.pickple.back.crew.exception.CrewExceptionCode.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.address.domain.MainAddress;
import kr.pickple.back.address.implement.AddressReader;
import kr.pickple.back.chat.exception.ChatException;
import kr.pickple.back.common.domain.RegistrationStatus;
import kr.pickple.back.crew.domain.Crew;
import kr.pickple.back.crew.domain.CrewMember;
import kr.pickple.back.crew.domain.CrewProfile;
import kr.pickple.back.crew.exception.CrewException;
import kr.pickple.back.crew.repository.CrewMemberRepository;
import kr.pickple.back.crew.repository.CrewRepository;
import kr.pickple.back.crew.repository.entity.CrewEntity;
import kr.pickple.back.crew.repository.entity.CrewMemberEntity;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.implement.MemberReader;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CrewReader {

    private final AddressReader addressReader;
    private final MemberReader memberReader;
    private final CrewRepository crewRepository;
    private final CrewMemberRepository crewMemberRepository;

    public Integer countByLeaderId(final Long leaderId) {
        return crewRepository.countByLeaderId(leaderId);
    }

    public Crew read(final Long crewId) {
        final CrewEntity crewEntity = getCrewById(crewId);

        return mapCrewEntityToDomain(crewEntity);
    }

    private CrewEntity getCrewById(final Long crewId) {
        return crewRepository.findById(crewId)
                .orElseThrow(() -> new CrewException(CREW_NOT_FOUND, crewId));
    }

    public Crew readByChatRoomId(final Long chatRoomId) {
        final CrewEntity crewEntity = crewRepository.findByChatRoomId(chatRoomId)
                .orElseThrow(() -> new ChatException(CHAT_CREW_NOT_FOUND, chatRoomId));

        return mapCrewEntityToDomain(crewEntity);
    }

    private Crew mapCrewEntityToDomain(final CrewEntity crewEntity) {
        final MainAddress mainAddress = addressReader.readMainAddressByIds(
                crewEntity.getAddressDepth1Id(),
                crewEntity.getAddressDepth2Id()
        );

        final Member leader = memberReader.readByMemberId(crewEntity.getLeaderId());

        return CrewMapper.mapCrewEntityToDomain(crewEntity, mainAddress, leader);
    }

    public List<Crew> readNearCrewsByAddress(
            final String addressDepth1Name,
            final String addressDepth2Name,
            final Pageable pageable
    ) {
        final MainAddress mainAddress = addressReader.readMainAddressByNames(addressDepth1Name, addressDepth2Name);
        final Page<CrewEntity> crewEntities = crewRepository.findByAddressDepth1IdAndAddressDepth2Id(
                mainAddress.getAddressDepth1Id(),
                mainAddress.getAddressDepth2Id(),
                pageable
        );

        return crewEntities.stream()
                .map(crewEntity -> CrewMapper.mapCrewEntityToDomain(
                                crewEntity,
                                mainAddress,
                                memberReader.readByMemberId(crewEntity.getLeaderId())
                        )
                ).toList();
    }

    public CrewMember readCrewMember(final Long memberId, final Long crewId) {
        final CrewMemberEntity crewMemberEntity = crewMemberRepository.findByMemberIdAndCrewId(memberId, crewId)
                .orElseThrow(() -> new CrewException(CREW_MEMBER_NOT_FOUND, memberId, crewId));
        final Member member = memberReader.readByMemberId(memberId);
        final Crew crew = read(crewId);

        return CrewMapper.mapCrewMemberEntityToDomain(crewMemberEntity, member, crew);
    }

    public List<Member> readAllMembersInStatus(final Long crewId, final RegistrationStatus status) {
        return crewMemberRepository.findAllByCrewIdAndStatus(crewId, status)
                .stream()
                .map(crewMemberEntity -> memberReader.readByMemberId(crewMemberEntity.getMemberId()))
                .toList();
    }

    public List<CrewProfile> readAllCrewProfilesByMemberIdAndStatus(
            final Long memberId,
            final RegistrationStatus memberStatus
    ) {
        final List<CrewEntity> crewEntities = crewMemberRepository.findAllByMemberIdAndStatus(memberId, memberStatus)
                .stream()
                .map(crewMember -> getCrewById(crewMember.getCrewId()))
                .toList();

        return mapCrewEntitiesToCrewProfiles(crewEntities, memberStatus);
    }

    public List<CrewProfile> readAllCrewProfilesByLeaderId(final Long memberId) {
        final List<CrewEntity> crewEntities = crewRepository.findAllByLeaderId(memberId);

        return mapCrewEntitiesToCrewProfiles(crewEntities, CONFIRMED);
    }

    private List<CrewProfile> mapCrewEntitiesToCrewProfiles(
            final List<CrewEntity> crewEntities,
            final RegistrationStatus registrationStatus
    ) {
        return crewEntities.stream()
                .map(crew -> {
                    final List<Member> members = readAllMembersInStatus(crew.getId(), registrationStatus);
                    final MainAddress mainAddress = addressReader.readMainAddressByIds(
                            crew.getAddressDepth1Id(),
                            crew.getAddressDepth2Id()
                    );
                    final Member leader = memberReader.readByMemberId(crew.getLeaderId());

                    return CrewMapper.mapCrewEntityToCrewProfile(crew, mainAddress, leader, members);
                })
                .toList();
    }
}
