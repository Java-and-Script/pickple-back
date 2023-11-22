package kr.pickple.back.member.service;

import static kr.pickple.back.common.domain.RegistrationStatus.*;
import static kr.pickple.back.member.exception.MemberExceptionCode.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.address.domain.AddressDepth1;
import kr.pickple.back.address.domain.AddressDepth2;
import kr.pickple.back.address.dto.response.MainAddressResponse;
import kr.pickple.back.address.service.AddressService;
import kr.pickple.back.auth.config.property.JwtProperties;
import kr.pickple.back.auth.domain.token.AuthTokens;
import kr.pickple.back.auth.domain.token.JwtProvider;
import kr.pickple.back.auth.domain.token.RefreshToken;
import kr.pickple.back.auth.repository.RedisRepository;
import kr.pickple.back.crew.dto.response.CrewProfileResponse;
import kr.pickple.back.fixture.domain.MemberFixtures;
import kr.pickple.back.fixture.dto.MemberDtoFixtures;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.dto.request.MemberCreateRequest;
import kr.pickple.back.member.dto.response.AuthenticatedMemberResponse;
import kr.pickple.back.member.dto.response.MemberProfileResponse;
import kr.pickple.back.member.exception.MemberException;
import kr.pickple.back.member.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private AddressService addressService;

    @Mock
    private JwtProperties jwtProperties;

    @Mock
    private RedisRepository redisRepository;

    @Test
    @Transactional
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
        final AuthTokens authTokens = AuthTokens.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .build();

        final Member member = memberCreateRequest.toEntity(mainAddressResponse);

        given(addressService.findMainAddressByNames(anyString(), anyString())).willReturn(mainAddressResponse);
        given(memberRepository.save(any(Member.class))).willReturn(member);
        given(jwtProvider.createLoginToken(anyString())).willReturn(authTokens);
        given(jwtProperties.getRefreshTokenExpirationTime()).willReturn(1000L);

        // when
        final AuthenticatedMemberResponse authenticatedMemberResponse = memberService.createMember(memberCreateRequest);

        // then
        verify(redisRepository).saveHash(anyString(), anyString(), any(RefreshToken.class), anyLong());
        assertThat(authenticatedMemberResponse).isNotNull();
    }

    @Test
    @DisplayName("회원을 조회할 수 있다.")
    void findMemberById_ReturnMemberProfileResponse() {
        // given
        final Long memberId = 1L;
        final Member member = buildMember();
        given(memberRepository.findById(anyLong())).willReturn(Optional.ofNullable(member));

        // when
        final MemberProfileResponse memberProfileResponse = memberService.findMemberProfileById(memberId);

        // then
        assertThat(memberProfileResponse).isNotNull();
    }

    @Test
    @DisplayName("회원이 가입한 크루 목록을 조회할 수 있다.")
    void findAllCrewsByMemberId_ReturnCrewProfileResponses() {
        // given
        final Long memberId = 1L;
        final Long loggedInMemberId = 1L;
        final Member member = buildMember();
        given(memberRepository.findById(anyLong())).willReturn(Optional.ofNullable(member));

        // when
        final List<CrewProfileResponse> crewProfileResponses = memberService.findAllCrewsByMemberId(memberId,
                loggedInMemberId, CONFIRMED);

        // then
        assertThat(crewProfileResponses).isNotNull();
    }

    @Test
    @DisplayName("회원이 만든 크루 목록을 조회할 수 있다.")
    void findCreatedCrewsByMemberId_ReturnCrewProfileResponses() {
        // given
        final Long memberId = 1L;
        final Long loggedInMemberId = 1L;
        final Member member = buildMember();

        given(memberRepository.findById(anyLong())).willReturn(Optional.ofNullable(member));

        // when
        final List<CrewProfileResponse> crewProfileResponses = memberService.findCreatedCrewsByMemberId(
                loggedInMemberId,
                memberId
        );

        // then
        assertThat(crewProfileResponses).isNotNull();
    }

    @Test
    @DisplayName("회원이 만든 크루 목록을 조회할 때 본인이 만든 크루가 아닌 경우 예외가 발생한다.")
    void findCreatedCrewsByMemberId_ThrowException() {
        // given
        final Long memberId = 1L;
        final Long loggedInMemberId = 2L;
        final Member member = buildMember();

        // when && then
        assertThatThrownBy(() -> memberService.findCreatedCrewsByMemberId(
                loggedInMemberId,
                memberId
        )).isInstanceOf(MemberException.class)
                .hasMessage(MEMBER_MISMATCH.getMessage());
    }

    private Member buildMember() {
        final AddressDepth1 addressDepth1 = AddressDepth1.builder()
                .name("서울시")
                .build();
        final AddressDepth2 addressDepth2 = AddressDepth2.builder()
                .name("영등포구")
                .addressDepth1(addressDepth1)
                .build();

        return MemberFixtures.memberBuild(addressDepth1, addressDepth2);
    }
}
