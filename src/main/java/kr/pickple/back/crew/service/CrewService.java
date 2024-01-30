package kr.pickple.back.crew.service;

import static kr.pickple.back.chat.domain.RoomType.*;
import static kr.pickple.back.common.domain.RegistrationStatus.*;
import static kr.pickple.back.crew.exception.CrewExceptionCode.*;

import java.text.MessageFormat;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.address.dto.response.MainAddressResponse;
import kr.pickple.back.address.service.AddressService;
import kr.pickple.back.chat.domain.ChatRoom;
import kr.pickple.back.chat.service.ChatRoomService;
import kr.pickple.back.common.config.property.S3Properties;
import kr.pickple.back.common.util.RandomUtil;
import kr.pickple.back.crew.domain.Crew;
import kr.pickple.back.crew.domain.CrewMember;
import kr.pickple.back.crew.dto.request.CrewCreateRequest;
import kr.pickple.back.crew.dto.response.CrewIdResponse;
import kr.pickple.back.crew.dto.response.CrewProfileResponse;
import kr.pickple.back.crew.exception.CrewException;
import kr.pickple.back.crew.repository.CrewMemberRepository;
import kr.pickple.back.crew.repository.CrewRepository;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.dto.response.MemberResponse;
import kr.pickple.back.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CrewService {

    private static final Integer CREW_IMAGE_START_NUMBER = 1;
    private static final Integer CREW_IMAGE_END_NUMBER = 20;
    private static final Integer CREW_CREATE_MAX_SIZE = 3;

    private final MemberRepository memberRepository;
    private final CrewRepository crewRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final AddressService addressService;
    private final ChatRoomService chatRoomService;
    private final S3Properties s3Properties;

    /**
     * 크루 생성
     */
    @Transactional
    public CrewIdResponse createCrew(final CrewCreateRequest crewCreateRequest, final Long loggedInMemberId) {
        validateIsDuplicatedCrewInfo(crewCreateRequest.getName());

        final Member leader = memberRepository.getMemberById(loggedInMemberId);

        validateMemberCreatedCrewsCount(leader);

        final MainAddressResponse mainAddressResponse = addressService.findMainAddressByNames(
                crewCreateRequest.getAddressDepth1(),
                crewCreateRequest.getAddressDepth2()
        );

        final Integer crewImageRandomNumber = RandomUtil.getRandomNumber(
                CREW_IMAGE_START_NUMBER,
                CREW_IMAGE_END_NUMBER
        );

        final Crew crew = crewCreateRequest.toEntity(
                leader,
                mainAddressResponse,
                MessageFormat.format(s3Properties.getCrewProfile(), crewImageRandomNumber),
                MessageFormat.format(s3Properties.getCrewBackground(), crewImageRandomNumber)
        );

        final CrewMember crewLeader = CrewMember.builder()
                .member(leader)
                .crew(crew)
                .build();

        crewLeader.confirmRegistration();
        crewMemberRepository.save(crewLeader);

        final ChatRoom chatRoom = chatRoomService.saveNewChatRoom(leader, crew.getName(), CREW);
        crew.makeNewCrewChatRoom(chatRoom);

        final Long crewId = crewRepository.save(crew).getId();

        return CrewIdResponse.from(crewId);
    }

    private void validateIsDuplicatedCrewInfo(final String name) {
        if (crewRepository.existsByName(name)) {
            throw new CrewException(CREW_IS_EXISTED, name);
        }
    }

    private void validateMemberCreatedCrewsCount(final Member leader) {
        final Long createdCrewsCount = leader.getCreatedCrewsCount();

        if (createdCrewsCount >= CREW_CREATE_MAX_SIZE) {
            throw new CrewException(CREW_CREATE_MAX_COUNT_EXCEEDED, createdCrewsCount);
        }
    }

    /**
     * 크루 상세 조회
     */
    public CrewProfileResponse findCrewById(final Long crewId) {
        final Crew crew = crewRepository.getCrewById(crewId);

        return CrewProfileResponse.of(crew, getConfirmedMemberResponses(crewId));
    }

    /**
     *  사용자 근처 크루 목록 조회
     */
    public List<CrewProfileResponse> findCrewsByAddress(
            final String addressDepth1,
            final String addressDepth2,
            final Pageable pageable
    ) {
        final MainAddressResponse mainAddressResponse = addressService.findMainAddressByNames(addressDepth1,
                addressDepth2);

        final Page<Crew> crews = crewRepository.findByAddressDepth1AndAddressDepth2(
                mainAddressResponse.getAddressDepth1(),
                mainAddressResponse.getAddressDepth2(),
                pageable
        );

        return crews.stream()
                .map(crew -> CrewProfileResponse.of(crew, getConfirmedMemberResponses(crew.getId())))
                .toList();
    }

    private List<MemberResponse> getConfirmedMemberResponses(final Long crewId) {
        return crewMemberRepository.findAllByCrewIdAndStatus(crewId, CONFIRMED)
                .stream()
                .map(CrewMember::getMember)
                .map(MemberResponse::from)
                .toList();
    }
}
