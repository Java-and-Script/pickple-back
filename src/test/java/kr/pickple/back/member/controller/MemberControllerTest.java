package kr.pickple.back.member.controller;

import static kr.pickple.back.position.domain.Position.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.pickple.back.auth.domain.token.AuthTokens;
import kr.pickple.back.auth.domain.token.JwtProvider;
import kr.pickple.back.fixture.domain.MemberPositionFixtures;
import kr.pickple.back.fixture.dto.MemberDtoFixtures;
import kr.pickple.back.fixture.setup.MemberPositionSetup;
import kr.pickple.back.fixture.setup.MemberSetup;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.domain.MemberPosition;
import kr.pickple.back.member.dto.request.MemberCreateRequest;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class MemberControllerTest {

    private static final String BASE_URL = "/members";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberSetup memberSetup;

    @Autowired
    private MemberPositionSetup memberPositionSetup;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private ObjectMapper objectMapper;

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
                .header("Authorization", "Bearer " + authTokens.getAccessToken())
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
                .andExpect(jsonPath("addressDepth2").value(memberCreateRequest.getAddressDepth2()))
                .andDo(print());
    }

    @Test
    @DisplayName("회원 프로필을 조회할 수 있다.")
    void findMemberById_ReturnMemberProfileResponse() throws Exception {
        // given
        final Member savedMember = memberSetup.save();

        final List<MemberPosition> memberPositions = MemberPositionFixtures.memberPositionsBuild(savedMember,
                List.of(CENTER, POINT_GUARD));
        final List<MemberPosition> savedPositions = memberPositionSetup.save(memberPositions);

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
                .andExpect(jsonPath("positions[0]").value(savedPositions.get(0).getPosition().getAcronym()))
                .andExpect(jsonPath("positions[1]").value(savedPositions.get(1).getPosition().getAcronym()))
                //TODO: 추후 Crew 도메인 완성 시, 해당 필드에 대한 로직 추가 예정 (11.4 황창현)
                .andDo(print());
    }
}
