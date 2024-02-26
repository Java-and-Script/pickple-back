package kr.pickple.back.crew.implement;

import static kr.pickple.back.common.domain.RegistrationStatus.*;
import static kr.pickple.back.crew.exception.CrewExceptionCode.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.address.dto.response.MainAddress;
import kr.pickple.back.address.implement.AddressReader;
import kr.pickple.back.common.domain.RegistrationStatus;
import kr.pickple.back.crew.domain.Crew;
import kr.pickple.back.crew.exception.CrewException;
import kr.pickple.back.crew.repository.CrewMemberRepository;
import kr.pickple.back.crew.repository.CrewRepository;
import kr.pickple.back.crew.repository.entity.CrewEntity;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CrewReader {

    private final AddressReader addressReader;
    private final CrewMapper crewMapper;
    private final CrewRepository crewRepository;
    private final CrewMemberRepository crewMemberRepository;

    public Integer countByLeaderId(final Long leaderId) {
        return crewRepository.countByLeaderId(leaderId);
    }

    public Crew read(final Long crewId, final RegistrationStatus status) {
        final CrewEntity crewEntity = crewRepository.findById(crewId)
                .orElseThrow(() -> new CrewException(CREW_NOT_FOUND, crewId));

        return crewMapper.mapCrewEntityToDomain(crewEntity, status);
    }

    public List<Crew> readJoinedCrewsByMemberId(final Long memberId) {
        return crewMemberRepository.findAllByMemberIdAndStatus(memberId, CONFIRMED)
                .stream()
                .map(crewMember -> read(crewMember.getCrewId(), CONFIRMED))
                .toList();
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
                .map(crewEntity -> crewMapper.mapCrewEntityToDomain(crewEntity, CONFIRMED))
                .toList();
    }
}
