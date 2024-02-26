package kr.pickple.back.crew.implement;

import static kr.pickple.back.common.domain.RegistrationStatus.*;
import static kr.pickple.back.crew.exception.CrewExceptionCode.*;

import java.util.List;

import org.springframework.stereotype.Component;

import kr.pickple.back.address.dto.response.MainAddress;
import kr.pickple.back.address.implement.AddressReader;
import kr.pickple.back.chat.repository.ChatRoomRepository;
import kr.pickple.back.common.domain.RegistrationStatus;
import kr.pickple.back.crew.domain.Crew;
import kr.pickple.back.crew.domain.CrewMember;
import kr.pickple.back.crew.domain.NewCrew;
import kr.pickple.back.crew.exception.CrewException;
import kr.pickple.back.crew.repository.CrewMemberRepository;
import kr.pickple.back.crew.repository.CrewRepository;
import kr.pickple.back.crew.repository.entity.CrewEntity;
import kr.pickple.back.crew.repository.entity.CrewMemberEntity;
import kr.pickple.back.member.domain.MemberDomain;
import kr.pickple.back.member.implement.MemberReader;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CrewMapper {

    private final AddressReader addressReader;
    private final MemberReader memberReader;
    private final CrewRepository crewRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final ChatRoomRepository chatRoomRepository;

    public CrewEntity mapNewCrewDomainToEntity(final NewCrew newCrew) {
        final MainAddress mainAddress = addressReader.readMainAddressByNames(
                newCrew.getAddressDepth1Name(),
                newCrew.getAddressDepth2Name()
        );

        return CrewEntity.builder()
                .name(newCrew.getName())
                .content(newCrew.getContent())
                .maxMemberCount(newCrew.getMaxMemberCount())
                .leaderId(newCrew.getLeader().getMemberId())
                .profileImageUrl(newCrew.getProfileImageUrl())
                .backgroundImageUrl(newCrew.getBackgroundImageUrl())
                .addressDepth1Id(mainAddress.getAddressDepth1().getId())
                .addressDepth2Id(mainAddress.getAddressDepth2().getId())
                .chatRoomId(newCrew.getChatRoom().getId())
                .build();
    }

    public Crew mapCrewEntityToDomain(final CrewEntity crewEntity, final RegistrationStatus status) {
        final MainAddress mainAddress = addressReader.readMainAddressById(
                crewEntity.getAddressDepth1Id(),
                crewEntity.getAddressDepth2Id()
        );

        final List<MemberDomain> members = crewMemberRepository.findAllByCrewIdAndStatus(crewEntity.getId(), status)
                .stream()
                .map(crewMember -> memberReader.readByMemberId(crewMember.getMemberId()))
                .toList();

        return Crew.builder()
                .crewId(crewEntity.getId())
                .name(crewEntity.getName())
                .content(crewEntity.getContent())
                .memberCount(crewEntity.getMemberCount())
                .maxMemberCount(crewEntity.getMaxMemberCount())
                .leader(memberReader.readByMemberId(crewEntity.getLeaderId()))
                .addressDepth1Name(mainAddress.getAddressDepth1().getName())
                .addressDepth2Name(mainAddress.getAddressDepth2().getName())
                .profileImageUrl(crewEntity.getProfileImageUrl())
                .backgroundImageUrl(crewEntity.getBackgroundImageUrl())
                .likeCount(crewEntity.getLikeCount())
                .competitionPoint(crewEntity.getCompetitionPoint())
                .chatRoom(chatRoomRepository.getChatRoomById(crewEntity.getChatRoomId()))
                .members(members)
                .build();
    }

    public CrewMemberEntity mapCrewMemberDomainToEntity(final CrewMember crewMember) {
        return CrewMemberEntity.builder()
                .status(crewMember.getStatus())
                .memberId(crewMember.getMember().getMemberId())
                .crewId(crewMember.getCrew().getCrewId())
                .build();
    }

    public CrewMember mapCrewMemberEntityToDomain(final CrewMemberEntity crewMemberEntity) {
        final CrewEntity crewEntity = crewRepository.findById(crewMemberEntity.getCrewId())
                .orElseThrow(() -> new CrewException(CREW_NOT_FOUND, crewMemberEntity.getCrewId()));

        return CrewMember.builder()
                .crewMemberId(crewMemberEntity.getId())
                .status(crewMemberEntity.getStatus())
                .member(memberReader.readByMemberId(crewMemberEntity.getMemberId()))
                .crew(mapCrewEntityToDomain(crewEntity, CONFIRMED))
                .build();
    }
}
