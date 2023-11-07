package kr.pickple.back.member.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.pickple.back.address.domain.AddressDepth1;
import kr.pickple.back.address.domain.AddressDepth2;
import kr.pickple.back.address.dto.response.MainAddressResponse;
import kr.pickple.back.address.service.AddressService;
import kr.pickple.back.auth.domain.token.AuthTokens;
import kr.pickple.back.auth.domain.token.JwtProvider;
import kr.pickple.back.auth.domain.token.RefreshToken;
import kr.pickple.back.auth.repository.RefreshTokenRepository;
import kr.pickple.back.fixture.domain.MemberFixtures;
import kr.pickple.back.fixture.dto.MemberDtoFixtures;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.domain.MemberPosition;
import kr.pickple.back.member.dto.request.MemberCreateRequest;
import kr.pickple.back.member.dto.response.AuthenticatedMemberResponse;
import kr.pickple.back.member.dto.response.MemberProfileResponse;
import kr.pickple.back.member.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private AddressService addressService;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    @DisplayName("회원을 생성할 수 있다.")
    void createMember_ReturnAuthenticatedMemberResponse() {
        // given
        final MemberCreateRequest memberCreateRequest = MemberDtoFixtures.memberCreateRequestBuild();
        final AddressDepth1 addressDepth1 = AddressDepth1.builder()
                .name("서울시")
                .build();
        final AddressDepth2 addressDepth2 = AddressDepth2.builder()
                .name("영등포구")
                .addressDepth1(addressDepth1)
                .build();
        final MainAddressResponse mainAddressResponse = MainAddressResponse.builder()
                .addressDepth1(addressDepth1)
                .addressDepth2(addressDepth2)
                .build();
        final AuthTokens authTokens = AuthTokens.builder().build();
        final Member member = memberCreateRequest.toEntity(mainAddressResponse);
        final List<MemberPosition> memberPositions = new ArrayList<>();

        given(addressService.findMainAddressByNames(anyString(), anyString())).willReturn(mainAddressResponse);
        given(memberRepository.save(any(Member.class))).willReturn(member);
        given(jwtProvider.createLoginToken(anyString())).willReturn(authTokens);
        given(refreshTokenRepository.save(any(RefreshToken.class))).will(invocation -> null);

        // when
        final AuthenticatedMemberResponse authenticatedMemberResponse = memberService.createMember(memberCreateRequest);

        // then
        assertThat(authenticatedMemberResponse).isNotNull();
    }

    @Test
    @DisplayName("회원을 조회할 수 있다.")
    void findMemberById_ReturnMemberProfileResponse() {
        // given
        final AddressDepth1 addressDepth1 = AddressDepth1.builder()
                .name("서울시")
                .build();
        final AddressDepth2 addressDepth2 = AddressDepth2.builder()
                .name("영등포구")
                .addressDepth1(addressDepth1)
                .build();

        final Member member = MemberFixtures.memberBuild(addressDepth1, addressDepth2);
        final List<MemberPosition> memberPositions = new ArrayList<>();

        given(memberRepository.findById(anyLong())).willReturn(Optional.ofNullable(member));

        // when
        MemberProfileResponse memberProfileResponse = memberService.findMemberProfileById(1L);

        // then
        assertThat(memberProfileResponse).isNotNull();
    }
}
