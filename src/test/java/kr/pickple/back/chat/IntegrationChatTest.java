package kr.pickple.back.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.pickple.back.auth.domain.token.JwtProvider;
import kr.pickple.back.auth.service.OauthService;
import kr.pickple.back.chat.service.ChatRoomService;
import kr.pickple.back.fixture.setup.MemberSetup;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public abstract class IntegrationChatTest {

    @Autowired
    protected JwtProvider jwtProvider;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected MemberSetup memberSetup;

    @Autowired
    protected ChatRoomService chatRoomService;
}
