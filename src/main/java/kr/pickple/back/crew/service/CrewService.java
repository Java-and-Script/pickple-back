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

import kr.pickple.back.address.dto.response.MainAddress;
import kr.pickple.back.address.implement.AddressReader;
import kr.pickple.back.chat.domain.ChatRoom;
import kr.pickple.back.chat.service.ChatRoomService;
import kr.pickple.back.common.config.property.S3Properties;
import kr.pickple.back.common.util.RandomUtil;
import kr.pickple.back.crew.domain.Crew;
import kr.pickple.back.crew.domain.CrewDomain;
import kr.pickple.back.crew.dto.response.CrewIdResponse;
import kr.pickple.back.crew.dto.response.CrewProfileResponse;
import kr.pickple.back.crew.exception.CrewException;
import kr.pickple.back.crew.implement.CrewWriter;
import kr.pickple.back.crew.repository.CrewMemberRepository;
import kr.pickple.back.crew.repository.CrewRepository;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.domain.MemberPosition;
import kr.pickple.back.member.dto.response.MemberResponse;
import kr.pickple.back.member.repository.MemberPositionRepository;
import kr.pickple.back.member.repository.MemberRepository;
import kr.pickple.back.position.domain.Position;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CrewService {

    private static final Integer CREW_IMAGE_START_NUMBER = 1;
    private static final Integer CREW_IMAGE_END_NUMBER = 20;
    private static final Integer CREW_CREATE_MAX_SIZE = 3;

    private final AddressReader addressReader;
    private final CrewWriter crewWriter;

    private final MemberRepository memberRepository;
    private final CrewRepository crewRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final MemberPositionRepository memberPositionRepository;
    private final ChatRoomService chatRoomService;
    private final S3Properties s3Properties;

    /**
     * 크루 생성
     */
    @Transactional
    public CrewIdResponse createCrew(final Long loggedInMemberId, final CrewDomain crew) {
        final Member leader = memberRepository.getMemberById(loggedInMemberId);
        validateCreateCrewMoreThanMaxCount(leader);

        final ChatRoom chatRoom = chatRoomService.saveNewChatRoom(leader, crew.getName(), CREW);
        chatRoom.updateMaxMemberCount(crew.getMaxMemberCount());

        crew.updateLeader(leader);
        crew.updateChatRoom(chatRoom);
        initializeNewCrewImages(crew);

        crewWriter.create(crew);
        crewWriter.register(leader, crew);

        return CrewIdResponse.from(crew.getCrewId());
    }

    private void validateCreateCrewMoreThanMaxCount(final Member leader) {
        final Integer createdCrewsCount = crewRepository.countByLeaderId(leader.getId());

        if (createdCrewsCount >= CREW_CREATE_MAX_SIZE) {
            throw new CrewException(CREW_CREATE_MAX_COUNT_EXCEEDED, createdCrewsCount);
        }
    }

    private void initializeNewCrewImages(final CrewDomain crew) {
        final Integer randomImageNumber = RandomUtil.getRandomNumber(CREW_IMAGE_START_NUMBER, CREW_IMAGE_END_NUMBER);

        crew.updateProfileImageUrl(MessageFormat.format(s3Properties.getCrewProfile(), randomImageNumber));
        crew.updateBackgroundImageUrl(MessageFormat.format(s3Properties.getCrewBackground(), randomImageNumber));
    }

    /**
     * 크루 상세 조회
     */
    public CrewProfileResponse findCrewById(final Long crewId) {
        final Crew crew = crewRepository.getCrewById(crewId);
        final MainAddress mainAddress = addressReader.readMainAddressById(
                crew.getAddressDepth1Id(),
                crew.getAddressDepth2Id()
        );

        return CrewProfileResponse.of(crew, getConfirmedMemberResponses(crewId), mainAddress);
    }

    /**
     *  사용자 근처 크루 목록 조회
     */
    public List<CrewProfileResponse> findCrewsByAddress(
            final String addressDepth1,
            final String addressDepth2,
            final Pageable pageable
    ) {
        final MainAddress mainAddress = addressReader.readMainAddressByNames(addressDepth1, addressDepth2);

        final Page<Crew> crews = crewRepository.findByAddressDepth1IdAndAddressDepth2Id(
                mainAddress.getAddressDepth1().getId(),
                mainAddress.getAddressDepth2().getId(),
                pageable
        );

        return crews.stream()
                .map(crew -> CrewProfileResponse.of(
                                crew,
                                getConfirmedMemberResponses(crew.getId()),
                                addressReader.readMainAddressById(crew.getAddressDepth1Id(), crew.getAddressDepth2Id())
                        )
                )
                .toList();
    }

    private List<MemberResponse> getConfirmedMemberResponses(final Long crewId) {
        return crewMemberRepository.findAllByCrewIdAndStatus(crewId, CONFIRMED)
                .stream()
                .map(crewMember -> memberRepository.getMemberById(crewMember.getMemberId()))
                .map(member -> MemberResponse.of(
                                member,
                                getPositionsByMember(member),
                                addressReader.readMainAddressById(member.getAddressDepth1Id(), member.getAddressDepth2Id())
                        )
                )
                .toList();
    }

    private List<Position> getPositionsByMember(final Member member) {
        final List<MemberPosition> memberPositions = memberPositionRepository.findAllByMemberId(member.getId());

        return Position.fromMemberPositions(memberPositions);
    }
}
