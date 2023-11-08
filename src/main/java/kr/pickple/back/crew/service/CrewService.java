package kr.pickple.back.crew.service;

import static kr.pickple.back.common.domain.RegistrationStatus.*;
import static kr.pickple.back.crew.exception.CrewExceptionCode.*;
import static kr.pickple.back.member.exception.MemberExceptionCode.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.address.dto.response.MainAddressResponse;
import kr.pickple.back.address.service.AddressService;
import kr.pickple.back.common.config.property.S3Properties;
import kr.pickple.back.crew.domain.Crew;
import kr.pickple.back.crew.dto.request.CrewCreateRequest;
import kr.pickple.back.crew.dto.response.CrewIdResponse;
import kr.pickple.back.crew.dto.response.CrewProfileResponse;
import kr.pickple.back.crew.exception.CrewException;
import kr.pickple.back.crew.exception.CrewExceptionCode;
import kr.pickple.back.crew.repository.CrewRepository;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.dto.response.MemberResponse;
import kr.pickple.back.member.exception.MemberException;
import kr.pickple.back.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CrewService {

    private final S3Properties s3Properties;
    private final CrewRepository crewRepository;
    private final AddressService addressService;
    private final MemberRepository memberRepository;

    @Transactional
    public CrewIdResponse createCrew(final CrewCreateRequest crewCreateRequest, final Long loggedInMemberId) {
        validateIsDuplicatedCrewInfo(crewCreateRequest.getName());

        final Member leader = memberRepository.findById(loggedInMemberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

        final MainAddressResponse mainAddressResponse = addressService.findMainAddressByNames(
                crewCreateRequest.getAddressDepth1(),
                crewCreateRequest.getAddressDepth2()
        );

        final Crew crew = crewCreateRequest.toEntity(leader, mainAddressResponse, s3Properties.getProfile(),
                s3Properties.getBackground());
        crew.addCrewMember(leader);

        final Long crewId = crewRepository.save(crew).getId();

        return CrewIdResponse.from(crewId);
    }

    public CrewProfileResponse findCrewById(final Long crewId) {
        final Crew crew = crewRepository.findById(crewId)
                .orElseThrow(() -> new CrewException(CrewExceptionCode.CREW_NOT_FOUND));

        final List<Member> confirmedCrewMembers = crew.getCrewMembers(CONFIRMED);
        final List<MemberResponse> crewMembers = confirmedCrewMembers.stream()
                .map(MemberResponse::from)
                .toList();

        return CrewProfileResponse.of(crew, crewMembers);
    }

    public List<CrewProfileResponse> findCrewByAddress(final String addressDepth1, final String addressDepth2,
            final Pageable pageable) {
        final MainAddressResponse mainAddressResponse = addressService.findMainAddressByNames(addressDepth1,
                addressDepth2);

        final Page<Crew> crews = crewRepository.findByAddressDepth1AndAddressDepth2(
                mainAddressResponse.getAddressDepth1(),
                mainAddressResponse.getAddressDepth2(),
                pageable
        );

        return crews.stream()
                .map(crew -> CrewProfileResponse.of(crew, crew.getCrewMembers(CONFIRMED).stream()
                        .map(MemberResponse::from)
                        .toList()))
                .toList();
    }

    private void validateIsDuplicatedCrewInfo(final String name) {
        if (crewRepository.existsByName(name)) {
            throw new CrewException(CREW_IS_EXISTED, name);
        }
    }
}
