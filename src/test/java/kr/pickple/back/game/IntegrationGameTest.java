package kr.pickple.back.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.pickple.back.auth.domain.token.JwtProvider;
import kr.pickple.back.fixture.setup.GameSetup;
import kr.pickple.back.fixture.setup.MemberSetup;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public abstract class IntegrationGameTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected JwtProvider jwtProvider;

    @Autowired
    protected GameSetup gameSetup;

    @Autowired
    protected MemberSetup memberSetup;

    @Autowired
    protected ObjectMapper objectMapper;
}
