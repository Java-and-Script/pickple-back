package kr.pickple.back.crew.implement;

import org.springframework.stereotype.Component;

import kr.pickple.back.address.dto.response.MainAddress;
import kr.pickple.back.address.implement.AddressReader;
import kr.pickple.back.crew.domain.Crew;
import kr.pickple.back.crew.domain.CrewDomain;
import kr.pickple.back.crew.domain.CrewMember;
import kr.pickple.back.crew.domain.CrewMemberDomain;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CrewMapper {

    private final AddressReader addressReader;

    public Crew mapCrewDomainToEntity(final CrewDomain crew) {
        final MainAddress mainAddress = addressReader.readMainAddressByNames(
                crew.getAddressDepth1Name(),
                crew.getAddressDepth2Name()
        );

        return Crew.builder()
                .name(crew.getName())
                .content(crew.getContent())
                .maxMemberCount(crew.getMaxMemberCount())
                .leaderId(crew.getLeader().getId())
                .profileImageUrl(crew.getProfileImageUrl())
                .backgroundImageUrl(crew.getBackgroundImageUrl())
                .addressDepth1Id(mainAddress.getAddressDepth1().getId())
                .addressDepth2Id(mainAddress.getAddressDepth2().getId())
                .chatRoomId(crew.getChatRoom().getId())
                .build();
    }

    public CrewMember mapCrewMemberDomainToEntity(final CrewMemberDomain crewMember) {
        return CrewMember.builder()
                .status(crewMember.getStatus())
                .memberId(crewMember.getMemberId())
                .crewId(crewMember.getCrewId())
                .build();
    }
}
