package kr.pickple.back.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.pickple.back.auth.domain.token.JwtProvider;
import kr.pickple.back.fixture.setup.CrewSetup;
import kr.pickple.back.fixture.setup.MemberSetup;

@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
public abstract class IntegrationMemberTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected MemberSetup memberSetup;

    @Autowired
    protected CrewSetup crewSetup;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected JwtProvider jwtProvider;
}
