package kr.pickple.back.crew;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.pickple.back.auth.domain.token.JwtProvider;
import kr.pickple.back.fixture.setup.CrewSetup;
import kr.pickple.back.fixture.setup.MemberSetup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public abstract class IntegrationCrewTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected JwtProvider jwtProvider;

    @Autowired
    protected CrewSetup crewSetup;

    @Autowired
    protected MemberSetup memberSetup;

    @Autowired
    protected ObjectMapper objectMapper;
}
