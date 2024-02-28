package kr.pickple.back.crew.service;

import static kr.pickple.back.chat.domain.RoomType.*;
import static kr.pickple.back.common.domain.RegistrationStatus.*;
import static kr.pickple.back.crew.exception.CrewExceptionCode.*;

import java.text.MessageFormat;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.chat.domain.ChatRoom;
import kr.pickple.back.chat.service.ChatRoomService;
import kr.pickple.back.common.config.property.S3Properties;
import kr.pickple.back.common.util.RandomUtil;
import kr.pickple.back.crew.domain.Crew;
import kr.pickple.back.crew.domain.CrewMember;
import kr.pickple.back.crew.domain.NewCrew;
import kr.pickple.back.crew.dto.mapper.CrewRequestMapper;
import kr.pickple.back.crew.dto.mapper.CrewResponseMapper;
import kr.pickple.back.crew.dto.request.CrewCreateRequest;
import kr.pickple.back.crew.dto.response.CrewIdResponse;
import kr.pickple.back.crew.dto.response.CrewProfileResponse;
import kr.pickple.back.crew.exception.CrewException;
import kr.pickple.back.crew.implement.CrewReader;
import kr.pickple.back.crew.implement.CrewWriter;
import kr.pickple.back.member.domain.MemberDomain;
import kr.pickple.back.member.implement.MemberReader;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CrewService {

    private static final Integer CREW_IMAGE_START_NUMBER = 1;
    private static final Integer CREW_IMAGE_END_NUMBER = 20;
    private static final Integer CREW_CREATE_MAX_SIZE = 3;

    private final MemberReader memberReader;
    private final CrewReader crewReader;
    private final CrewWriter crewWriter;

    private final ChatRoomService chatRoomService;
    private final S3Properties s3Properties;

    /**
     * 크루 생성
     */
    @Transactional
    public CrewIdResponse createCrew(final Long loggedInMemberId, final CrewCreateRequest crewCreateRequest) {
        final NewCrew newCrew = CrewRequestMapper.mapToNewCrewDomain(crewCreateRequest);

        final MemberDomain leader = memberReader.readByMemberId(loggedInMemberId);
        validateCreateCrewMoreThanMaxCount(loggedInMemberId);

        final ChatRoom chatRoom = chatRoomService.saveNewChatRoom(leader, newCrew.getName(), CREW);
        chatRoom.updateMaxMemberCount(newCrew.getMaxMemberCount());

        newCrew.assignLeader(leader);
        newCrew.assignChatRoom(chatRoom);
        assignImageUrls(newCrew);

        final Crew crew = crewWriter.create(newCrew);
        final CrewMember crewLeader = crewWriter.register(leader, crew);
        crewWriter.updateMemberRegistrationStatus(crewLeader, CONFIRMED);

        return CrewResponseMapper.mapToCrewIdResponseDto(crew.getCrewId());
    }

    private void validateCreateCrewMoreThanMaxCount(final Long leaderId) {
        final Integer createdCrewsCount = crewReader.countByLeaderId(leaderId);

        if (createdCrewsCount >= CREW_CREATE_MAX_SIZE) {
            throw new CrewException(CREW_CREATE_MAX_COUNT_EXCEEDED, createdCrewsCount);
        }
    }

    private void assignImageUrls(final NewCrew newCrew) {
        final Integer randomImageNumber = RandomUtil.getRandomNumber(CREW_IMAGE_START_NUMBER, CREW_IMAGE_END_NUMBER);
        final String profileImageUrl = MessageFormat.format(s3Properties.getCrewProfile(), randomImageNumber);
        final String backgroundImageUrl = MessageFormat.format(s3Properties.getCrewBackground(), randomImageNumber);

        newCrew.assignImageUrls(profileImageUrl, backgroundImageUrl);
    }

    /**
     * 크루 상세 조회
     */
    public CrewProfileResponse findCrewById(final Long crewId) {
        final Crew crew = crewReader.read(crewId, CONFIRMED);

        return CrewResponseMapper.mapToCrewProfileResponseDto(crew);
    }

    /**
     *  사용자 근처 크루 목록 조회
     */
    public List<CrewProfileResponse> findNearCrewsByAddress(
            final String addressDepth1Name,
            final String addressDepth2Name,
            final Pageable pageable
    ) {
        return crewReader.readNearCrewsByAddress(addressDepth1Name, addressDepth2Name, pageable)
                .stream()
                .map(CrewResponseMapper::mapToCrewProfileResponseDto)
                .toList();
    }
}
