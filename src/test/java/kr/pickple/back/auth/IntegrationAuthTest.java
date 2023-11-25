package kr.pickple.back.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import kr.pickple.back.auth.domain.token.JwtProvider;
import kr.pickple.back.auth.service.OauthService;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public abstract class IntegrationAuthTest {

    @Autowired
    protected JwtProvider jwtProvider;

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    protected OauthService oauthService;
}
