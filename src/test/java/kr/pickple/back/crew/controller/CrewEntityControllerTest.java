package kr.pickple.back.crew.controller;

import kr.pickple.back.address.repository.entity.AddressDepth1Entity;
import kr.pickple.back.address.repository.entity.AddressDepth2Entity;
import kr.pickple.back.auth.domain.token.AuthTokens;
import kr.pickple.back.crew.repository.entity.CrewEntity;
import kr.pickple.back.crew.dto.request.CrewMemberUpdateStatusRequest;
import kr.pickple.back.crew.IntegrationCrewTest;
import kr.pickple.back.crew.repository.CrewMemberRepository;
import kr.pickple.back.fixture.dto.CrewDtoFixtures;
import kr.pickple.back.fixture.setup.AddressSetup;
import kr.pickple.back.member.repository.entity.MemberEntity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static kr.pickple.back.common.domain.RegistrationStatus.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
public class CrewEntityControllerTest extends IntegrationCrewTest {

    private static final String BASE_URL = "/crews";

    @Autowired
    private AddressSetup addressSetup;

    @Autowired
    private CrewMemberRepository crewMemberRepository;

    @Test
    @DisplayName("사용자는 해당 크루의 상세 정보를 조회할 수 있다.")
    void findCrewDetailsById_ReturnCrewResponse() throws Exception {
        //given
        final CrewEntity crew = crewSetup.saveWithConfirmedMembers(2);
        final MemberEntity crewLeader = crew.getLeader();
        final MemberEntity crewMember = crewMemberRepository.findAllByCrewIdAndStatus(crew.getId(), CONFIRMED)
                .get(1)
                .getMember();

        //when
        final ResultActions resultActions = mockMvc.perform(get(BASE_URL + "/{crewId}", crew.getId()));

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id").value(crew.getId()))
                .andExpect(jsonPath("name").value(crew.getName()))
                .andExpect(jsonPath("content").value(crew.getContent()))
                .andExpect(jsonPath("memberCount").value(crew.getMemberCount()))
                .andExpect(jsonPath("maxMemberCount").value(crew.getMaxMemberCount()))
                .andExpect(jsonPath("profileImageUrl").value(crew.getProfileImageUrl()))
                .andExpect(jsonPath("backgroundImageUrl").value(crew.getBackgroundImageUrl()))
                .andExpect(jsonPath("status").value(crew.getStatus().getDescription()))
                .andExpect(jsonPath("likeCount").value(crew.getLikeCount()))
                .andExpect(jsonPath("competitionPoint").value(crew.getCompetitionPoint()))
                .andExpect(jsonPath("leader.id").value(crewLeader.getId()))
                .andExpect(jsonPath("leader.email").value(crewLeader.getEmail()))
                .andExpect(jsonPath("leader.nickname").value(crewLeader.getNickname()))
                .andExpect(jsonPath("leader.introduction").value(crewLeader.getIntroduction()))
                .andExpect(jsonPath("leader.profileImageUrl").value(crewLeader.getProfileImageUrl()))
                .andExpect(jsonPath("leader.mannerScore").value(crewLeader.getMannerScore()))
                .andExpect(jsonPath("leader.mannerScoreCount").value(crewLeader.getMannerScoreCount()))
                .andExpect(jsonPath("leader.addressDepth1").value(crewLeader.getAddressDepth1().getName()))
                .andExpect(jsonPath("leader.addressDepth2").value(crewLeader.getAddressDepth2().getName()))
                .andExpect(jsonPath("leader.positions[0]").value(crewLeader.getPositions().get(0).getAcronym()))
                .andExpect(jsonPath("leader.positions[1]").value(crewLeader.getPositions().get(1).getAcronym()))
                .andExpect(jsonPath("members[0].id").value(crewLeader.getId()))
                .andExpect(jsonPath("members[1].id").value(crewMember.getId()))
                .andDo(print());
    }

    @Test
    @DisplayName("사용자는 크루 모집글에 참여 신청을 할 수 있다.")
    void applyForCrewMemberShip_Success() throws Exception {
        //given
        final List<MemberEntity> members = memberSetup.save(2);
        final MemberEntity crewLeader = members.get(0);
        final MemberEntity crewApplyMember = members.get(1);
        final CrewEntity crew = crewSetup.save(crewLeader);

        final String subject = String.valueOf(crewApplyMember.getId());
        final AuthTokens authTokens = jwtProvider.createLoginToken(subject);

        //when
        final ResultActions resultActions = mockMvc.perform(
                post(BASE_URL + "/{crewId}/members", crew.getId())
                        .header(AUTHORIZATION, "Bearer " + authTokens.getAccessToken())
        );

        //then
        resultActions.andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("크루장은 크루 모집글에 참여 신청한 사용자 정보 목록을 조회할 수 있다.")
    void findAllCrewMembers_WaitingStatus_ReturnCrewResponse() throws Exception {
        //given
        final CrewEntity crew = crewSetup.saveWithWaitingMembers(2);
        final MemberEntity crewLeader = crew.getLeader();
        final MemberEntity crewMember = crewMemberRepository.findAllByCrewIdAndStatus(crew.getId(), WAITING)
                .get(0)
                .getMember();

        final String subject = String.valueOf(crewLeader.getId());
        final AuthTokens authTokens = jwtProvider.createLoginToken(subject);

        //when
        final ResultActions resultActions = mockMvc.perform(
                get(BASE_URL + "/{crewId}/members", crew.getId())
                        .param("status", WAITING.getDescription())
                        .header(AUTHORIZATION, "Bearer " + authTokens.getAccessToken())
        );

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id").value(crew.getId()))
                .andExpect(jsonPath("name").value(crew.getName()))
                .andExpect(jsonPath("content").value(crew.getContent()))
                .andExpect(jsonPath("memberCount").value(crew.getMemberCount()))
                .andExpect(jsonPath("maxMemberCount").value(crew.getMaxMemberCount()))
                .andExpect(jsonPath("profileImageUrl").value(crew.getProfileImageUrl()))
                .andExpect(jsonPath("backgroundImageUrl").value(crew.getBackgroundImageUrl()))
                .andExpect(jsonPath("status").value(crew.getStatus().getDescription()))
                .andExpect(jsonPath("likeCount").value(crew.getLikeCount()))
                .andExpect(jsonPath("competitionPoint").value(crew.getCompetitionPoint()))
                .andExpect(jsonPath("leader.id").value(crewLeader.getId()))
                .andExpect(jsonPath("leader.email").value(crewLeader.getEmail()))
                .andExpect(jsonPath("leader.nickname").value(crewLeader.getNickname()))
                .andExpect(jsonPath("leader.introduction").value(crewLeader.getIntroduction()))
                .andExpect(jsonPath("leader.profileImageUrl").value(crewLeader.getProfileImageUrl()))
                .andExpect(jsonPath("leader.mannerScore").value(crewLeader.getMannerScore()))
                .andExpect(jsonPath("leader.mannerScoreCount").value(crewLeader.getMannerScoreCount()))
                .andExpect(jsonPath("leader.addressDepth1").value(crewLeader.getAddressDepth1().getName()))
                .andExpect(jsonPath("leader.addressDepth2").value(crewLeader.getAddressDepth2().getName()))
                .andExpect(jsonPath("leader.positions[0]").value(crewLeader.getPositions().get(0).getAcronym()))
                .andExpect(jsonPath("leader.positions[1]").value(crewLeader.getPositions().get(1).getAcronym()))
                .andExpect(jsonPath("members[0].id").value(crewMember.getId()))
                .andDo(print());
    }

    @Test
    @DisplayName("크루장은 다른 사용자의 크루 모집글 참여 신청을 수락할 수 있다.")
    void updateCrewMemberRegistrationStatus_Success() throws Exception {
        //given
        final CrewEntity crew = crewSetup.saveWithWaitingMembers(2);
        final MemberEntity crewLeader = crew.getLeader();
        final MemberEntity crewMember = crewMemberRepository.findAllByCrewIdAndStatus(crew.getId(), WAITING)
                .get(0)
                .getMember();

        final String subject = String.valueOf(crewLeader.getId());
        final AuthTokens authTokens = jwtProvider.createLoginToken(subject);

        final CrewMemberUpdateStatusRequest crewMemberUpdateStatusRequest = CrewDtoFixtures
                .crewMemberUpdateStatusRequest(CONFIRMED);
        final String requestBody = objectMapper.writeValueAsString(crewMemberUpdateStatusRequest);

        //when
        final ResultActions resultActions = mockMvc.perform(
                patch(BASE_URL + "/{crewId}/members/{memberId}", crew.getId(), crewMember.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, "Bearer " + authTokens.getAccessToken())
                        .content(requestBody)
        );

        //then
        resultActions.andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("크루장은 다른 사용자의 크루 모집글 참여 신청을 거절할 수 있다.")
    void deleteCrewMember_CrewLeader_Success() throws Exception {
        //given
        final CrewEntity crew = crewSetup.saveWithWaitingMembers(2);
        final MemberEntity crewLeader = crew.getLeader();
        final MemberEntity crewMember = crewMemberRepository.findAllByCrewIdAndStatus(crew.getId(), WAITING)
                .get(0)
                .getMember();

        final String subject = String.valueOf(crewLeader.getId());
        final AuthTokens authTokens = jwtProvider.createLoginToken(subject);

        //when
        final ResultActions resultActions = mockMvc.perform(
                delete(BASE_URL + "/{crewId}/members/{memberId}", crew.getId(), crewMember.getId())
                        .header(AUTHORIZATION, "Bearer " + authTokens.getAccessToken())
        );

        //then
        resultActions.andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("사용자는 자신 위치 근처의 크루를 조회할 수 있다.")
    void findCrewsByAddress_Success() throws Exception {
        //given
        final CrewEntity crew = crewSetup.saveWithConfirmedMembers(2);
        final AddressDepth1Entity addressDepth1 = addressSetup.findAddressDepth1("서울시");
        final AddressDepth2Entity addressDepth2 = addressSetup.findAddressDepth2("영등포구");
        final MemberEntity crewLeader = crew.getLeader();
        final MemberEntity crewMember = crewMemberRepository.findAllByCrewIdAndStatus(crew.getId(), CONFIRMED)
                .get(1)
                .getMember();

        //when
        final ResultActions resultActions = mockMvc.perform(
                get(BASE_URL)
                        .param("addressDepth1", addressDepth1.getName())
                        .param("addressDepth2", addressDepth2.getName())
                        .param("page", "0")
                        .param("size", "10")
        );

        //then
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(crew.getId()))
                .andExpect(jsonPath("$[0].name").value(crew.getName()))
                .andExpect(jsonPath("$[0].content").value(crew.getContent()))
                .andExpect(jsonPath("$[0].memberCount").value(crew.getMemberCount()))
                .andExpect(jsonPath("$[0].maxMemberCount").value(crew.getMaxMemberCount()))
                .andExpect(jsonPath("$[0].profileImageUrl").value(crew.getProfileImageUrl()))
                .andExpect(jsonPath("$[0].backgroundImageUrl").value(crew.getBackgroundImageUrl()))
                .andExpect(jsonPath("$[0].status").value(crew.getStatus().getDescription()))
                .andExpect(jsonPath("$[0].likeCount").value(crew.getLikeCount()))
                .andExpect(jsonPath("$[0].competitionPoint").value(crew.getCompetitionPoint()))
                .andExpect(jsonPath("$[0].leader.id").value(crewLeader.getId()))
                .andExpect(jsonPath("$[0].leader.email").value(crewLeader.getEmail()))
                .andExpect(jsonPath("$[0].leader.nickname").value(crewLeader.getNickname()))
                .andExpect(jsonPath("$[0].leader.introduction").value(crewLeader.getIntroduction()))
                .andExpect(jsonPath("$[0].leader.profileImageUrl").value(crewLeader.getProfileImageUrl()))
                .andExpect(jsonPath("$[0].leader.mannerScore").value(crewLeader.getMannerScore()))
                .andExpect(jsonPath("$[0].leader.mannerScoreCount").value(crewLeader.getMannerScoreCount()))
                .andExpect(jsonPath("$[0].leader.addressDepth1").value(crewLeader.getAddressDepth1().getName()))
                .andExpect(jsonPath("$[0].leader.addressDepth2").value(crewLeader.getAddressDepth2().getName()))
                .andExpect(jsonPath("$[0].leader.positions[0]").value(crewLeader.getPositions().get(0).getAcronym()))
                .andExpect(jsonPath("$[0].leader.positions[1]").value(crewLeader.getPositions().get(1).getAcronym()))
                .andExpect(jsonPath("$[0].members[0].id").value(crewLeader.getId()))
                .andExpect(jsonPath("$[0].members[1].id").value(crewMember.getId()))
                .andDo(print());
    }
}
