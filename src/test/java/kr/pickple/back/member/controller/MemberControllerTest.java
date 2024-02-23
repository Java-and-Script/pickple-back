package kr.pickple.back.member.controller;

import static org.springframework.http.HttpHeaders.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.auth.domain.token.AuthTokens;
import kr.pickple.back.crew.repository.entity.CrewEntity;
import kr.pickple.back.fixture.dto.MemberDtoFixtures;
import kr.pickple.back.member.IntegrationMemberTest;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.dto.request.MemberCreateRequest;

@Transactional
class MemberControllerTest extends IntegrationMemberTest {

    private static final String BASE_URL = "/members";

    @Test
    @DisplayName("회원을 생성할 수 있다.")
    void createMemberById_ReturnAuthenticatedMemberResponse() throws Exception {
        // given
        final MemberCreateRequest memberCreateRequest = MemberDtoFixtures.memberCreateRequestBuild();
        final String requestBody = objectMapper.writeValueAsString(memberCreateRequest);

        final String subject = memberCreateRequest.getOauthProvider().toString() + memberCreateRequest.getOauthId();
        final AuthTokens authTokens = jwtProvider.createRegisterToken(subject);

        // when
        final ResultActions resultActions = mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .header(AUTHORIZATION, "Bearer " + authTokens.getAccessToken())
        );

        // then
        resultActions.andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(cookie().httpOnly("refresh-token", true))
                .andExpect(cookie().secure("refresh-token", true))
                .andExpect(cookie().exists("refresh-token"))
                .andExpect(jsonPath("accessToken").isString())
                .andExpect(jsonPath("refreshToken").isString())
                .andExpect(jsonPath("id").isNumber())
                .andExpect(jsonPath("nickname").value(memberCreateRequest.getNickname()))
                .andExpect(jsonPath("profileImageUrl").value(memberCreateRequest.getProfileImageUrl()))
                .andExpect(jsonPath("email").value(memberCreateRequest.getEmail()))
                .andExpect(jsonPath("oauthId").value(memberCreateRequest.getOauthId()))
                .andExpect(jsonPath("oauthProvider").value(memberCreateRequest.getOauthProvider().name()))
                .andExpect(jsonPath("addressDepth1").value(memberCreateRequest.getAddressDepth1()))
                .andExpect(jsonPath("addressDepth2").value(memberCreateRequest.getAddressDepth2()));
    }

    @Test
    @DisplayName("회원 프로필을 조회할 수 있다.")
    void findMemberById_ReturnMemberProfileResponse() throws Exception {
        // given
        final Member savedMember = memberSetup.save();
        final CrewEntity savedCrew = crewSetup.save(savedMember);

        // when
        final ResultActions resultActions = mockMvc.perform(get(BASE_URL + "/{members}", savedMember.getId()));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id").value(savedMember.getId()))
                .andExpect(jsonPath("email").value(savedMember.getEmail()))
                .andExpect(jsonPath("nickname").value(savedMember.getNickname()))
                .andExpect(jsonPath("introduction").value(savedMember.getIntroduction()))
                .andExpect(jsonPath("profileImageUrl").value(savedMember.getProfileImageUrl()))
                .andExpect(jsonPath("mannerScore").value(savedMember.getMannerScore()))
                .andExpect(jsonPath("mannerScoreCount").value(savedMember.getMannerScoreCount()))
                .andExpect(jsonPath("addressDepth1").value(savedMember.getAddressDepth1().getName()))
                .andExpect(jsonPath("addressDepth2").value(savedMember.getAddressDepth2().getName()))
                .andExpect(jsonPath("positions[0]").value(savedMember.getPositions().get(0).getAcronym()))
                .andExpect(jsonPath("positions[1]").value(savedMember.getPositions().get(1).getAcronym()))
                .andExpect(jsonPath("crews[0].id").value(savedCrew.getId()))
                .andExpect(jsonPath("crews[0].name").value(savedCrew.getName()))
                .andExpect(jsonPath("crews[0].content").value(savedCrew.getContent()))
                .andExpect(jsonPath("crews[0].memberCount").value(savedCrew.getMemberCount()))
                .andExpect(jsonPath("crews[0].maxMemberCount").value(savedCrew.getMaxMemberCount()))
                .andExpect(jsonPath("crews[0].profileImageUrl").value(savedCrew.getProfileImageUrl()))
                .andExpect(jsonPath("crews[0].backgroundImageUrl").value(savedCrew.getBackgroundImageUrl()))
                .andExpect(jsonPath("crews[0].status").value(savedCrew.getStatus().getDescription()))
                .andExpect(jsonPath("crews[0].likeCount").value(savedCrew.getLikeCount()))
                .andExpect(jsonPath("crews[0].competitionPoint").value(savedCrew.getCompetitionPoint()))
                .andExpect(jsonPath("crews[0].leader.id").value(savedMember.getId()))
                .andExpect(jsonPath("crews[0].leader.nickname").value(savedMember.getNickname()))
                .andExpect(jsonPath("crews[0].leader.email").value(savedMember.getEmail()))
                .andExpect(jsonPath("crews[0].leader.introduction").value(savedMember.getIntroduction()))
                .andExpect(jsonPath("crews[0].leader.profileImageUrl").value(savedMember.getProfileImageUrl()))
                .andExpect(jsonPath("crews[0].leader.mannerScore").value(savedMember.getMannerScore()))
                .andExpect(jsonPath("crews[0].leader.mannerScoreCount").value(savedMember.getMannerScoreCount()))
                .andExpect(jsonPath("crews[0].leader.addressDepth1").value(savedMember.getAddressDepth1().getName()))
                .andExpect(jsonPath("crews[0].leader.addressDepth2").value(savedMember.getAddressDepth2().getName()))
                .andExpect(
                        jsonPath("crews[0].leader.positions[0]").value(savedMember.getPositions().get(0).getAcronym()))
                .andExpect(
                        jsonPath("crews[0].leader.positions[1]").value(savedMember.getPositions().get(1).getAcronym()))
                .andExpect(jsonPath("crews[0].addressDepth1").value(savedCrew.getAddressDepth1().getName()))
                .andExpect(jsonPath("crews[0].addressDepth2").value(savedCrew.getAddressDepth2().getName()));
    }

    @Test
    @DisplayName("회원이 가입한 크루 목록을 조회할 수 있다.")
    void findAllCrewsByMemberId_ReturnCrewProfileResponses() throws Exception {
        // given
        final Member member = memberSetup.save();
        final CrewEntity crew = crewSetup.save(member);

        final AuthTokens authTokens = jwtProvider.createLoginToken(String.valueOf(member.getId()));

        // when
        final ResultActions resultActions = mockMvc.perform(get("/members/{memberId}/crews", member.getId())
                .header(AUTHORIZATION, "Bearer " + authTokens.getAccessToken())
                .queryParam("status", "확정")
        );

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("[0].id").value(crew.getId()))
                .andExpect(jsonPath("[0].name").value(crew.getName()))
                .andExpect(jsonPath("[0].content").value(crew.getContent()))
                .andExpect(jsonPath("[0].memberCount").value(crew.getMemberCount()))
                .andExpect(jsonPath("[0].maxMemberCount").value(crew.getMaxMemberCount()))
                .andExpect(jsonPath("[0].profileImageUrl").value(crew.getProfileImageUrl()))
                .andExpect(jsonPath("[0].backgroundImageUrl").value(crew.getBackgroundImageUrl()))
                .andExpect(jsonPath("[0].status").value(crew.getStatus().getDescription()))
                .andExpect(jsonPath("[0].likeCount").value(crew.getLikeCount()))
                .andExpect(jsonPath("[0].competitionPoint").value(crew.getCompetitionPoint()))
                .andExpect(jsonPath("[0].leader.id").value(member.getId()))
                .andExpect(jsonPath("[0].leader.nickname").value(member.getNickname()))
                .andExpect(jsonPath("[0].leader.email").value(member.getEmail()))
                .andExpect(jsonPath("[0].leader.introduction").value(member.getIntroduction()))
                .andExpect(jsonPath("[0].leader.profileImageUrl").value(member.getProfileImageUrl()))
                .andExpect(jsonPath("[0].leader.mannerScore").value(member.getMannerScore()))
                .andExpect(jsonPath("[0].leader.mannerScoreCount").value(member.getMannerScoreCount()))
                .andExpect(jsonPath("[0].leader.addressDepth1").value(member.getAddressDepth1().getName()))
                .andExpect(jsonPath("[0].leader.addressDepth2").value(member.getAddressDepth2().getName()))
                .andExpect(jsonPath("[0].leader.positions[0]").value(member.getPositions().get(0).getAcronym()))
                .andExpect(jsonPath("[0].leader.positions[1]").value(member.getPositions().get(1).getAcronym()))
                .andExpect(jsonPath("[0].addressDepth1").value(crew.getAddressDepth1().getName()))
                .andExpect(jsonPath("[0].addressDepth2").value(crew.getAddressDepth2().getName()))
                .andExpect(jsonPath("[0].members[0].id").value(member.getId()))
                .andExpect(jsonPath("[0].members[0].nickname").value(member.getNickname()))
                .andExpect(jsonPath("[0].members[0].email").value(member.getEmail()))
                .andExpect(jsonPath("[0].members[0].introduction").value(member.getIntroduction()))
                .andExpect(jsonPath("[0].members[0].profileImageUrl").value(member.getProfileImageUrl()))
                .andExpect(jsonPath("[0].members[0].mannerScore").value(member.getMannerScore()))
                .andExpect(jsonPath("[0].members[0].mannerScoreCount").value(member.getMannerScoreCount()))
                .andExpect(jsonPath("[0].members[0].addressDepth1").value(member.getAddressDepth1().getName()))
                .andExpect(jsonPath("[0].members[0].addressDepth2").value(member.getAddressDepth2().getName()));
    }

    @Test
    @DisplayName("회원이 만든 크루 목록을 조회할 수 있다.")
    void findCreatedCrewsByMemberId_ReturnCrewProfileResponses() throws Exception {
        // given
        final Member member = memberSetup.save();
        final CrewEntity crew = crewSetup.save(member);

        final AuthTokens authTokens = jwtProvider.createLoginToken(String.valueOf(member.getId()));

        // when
        final ResultActions resultActions = mockMvc.perform(get("/members/{memberId}/created-crews", member.getId())
                .header(AUTHORIZATION, "Bearer " + authTokens.getAccessToken())
        );

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("[0].id").value(crew.getId()))
                .andExpect(jsonPath("[0].name").value(crew.getName()))
                .andExpect(jsonPath("[0].content").value(crew.getContent()))
                .andExpect(jsonPath("[0].memberCount").value(crew.getMemberCount()))
                .andExpect(jsonPath("[0].maxMemberCount").value(crew.getMaxMemberCount()))
                .andExpect(jsonPath("[0].profileImageUrl").value(crew.getProfileImageUrl()))
                .andExpect(jsonPath("[0].backgroundImageUrl").value(crew.getBackgroundImageUrl()))
                .andExpect(jsonPath("[0].status").value(crew.getStatus().getDescription()))
                .andExpect(jsonPath("[0].likeCount").value(crew.getLikeCount()))
                .andExpect(jsonPath("[0].competitionPoint").value(crew.getCompetitionPoint()))
                .andExpect(jsonPath("[0].leader.id").value(member.getId()))
                .andExpect(jsonPath("[0].leader.nickname").value(member.getNickname()))
                .andExpect(jsonPath("[0].leader.email").value(member.getEmail()))
                .andExpect(jsonPath("[0].leader.introduction").value(member.getIntroduction()))
                .andExpect(jsonPath("[0].leader.profileImageUrl").value(member.getProfileImageUrl()))
                .andExpect(jsonPath("[0].leader.mannerScore").value(member.getMannerScore()))
                .andExpect(jsonPath("[0].leader.mannerScoreCount").value(member.getMannerScoreCount()))
                .andExpect(jsonPath("[0].leader.addressDepth1").value(member.getAddressDepth1().getName()))
                .andExpect(jsonPath("[0].leader.addressDepth2").value(member.getAddressDepth2().getName()))
                .andExpect(jsonPath("[0].leader.positions[0]").value(member.getPositions().get(0).getAcronym()))
                .andExpect(jsonPath("[0].leader.positions[1]").value(member.getPositions().get(1).getAcronym()))
                .andExpect(jsonPath("[0].addressDepth1").value(crew.getAddressDepth1().getName()))
                .andExpect(jsonPath("[0].addressDepth2").value(crew.getAddressDepth2().getName()))
                .andExpect(jsonPath("[0].members[0].id").value(member.getId()))
                .andExpect(jsonPath("[0].members[0].nickname").value(member.getNickname()))
                .andExpect(jsonPath("[0].members[0].email").value(member.getEmail()))
                .andExpect(jsonPath("[0].members[0].introduction").value(member.getIntroduction()))
                .andExpect(jsonPath("[0].members[0].profileImageUrl").value(member.getProfileImageUrl()))
                .andExpect(jsonPath("[0].members[0].mannerScore").value(member.getMannerScore()))
                .andExpect(jsonPath("[0].members[0].mannerScoreCount").value(member.getMannerScoreCount()))
                .andExpect(jsonPath("[0].members[0].addressDepth1").value(member.getAddressDepth1().getName()))
                .andExpect(jsonPath("[0].members[0].addressDepth2").value(member.getAddressDepth2().getName()));
    }
}
