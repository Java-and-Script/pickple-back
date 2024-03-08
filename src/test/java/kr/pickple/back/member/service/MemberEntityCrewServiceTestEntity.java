package kr.pickple.back.member.service;

import static kr.pickple.back.common.domain.RegistrationStatus.*;
import static kr.pickple.back.member.exception.MemberExceptionCode.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import kr.pickple.back.address.domain.AddressDepth1;
import kr.pickple.back.address.domain.AddressDepth2;
import kr.pickple.back.crew.dto.response.CrewProfileResponse;
import kr.pickple.back.fixture.domain.MemberFixtures;
import kr.pickple.back.member.repository.entity.MemberEntity;
import kr.pickple.back.member.exception.MemberException;
import kr.pickple.back.member.repository.MemberRepository;

public class MemberEntityCrewServiceTestEntity {

    @InjectMocks
    private MemberCrewService memberCrewService;

    @Mock
    private MemberRepository memberRepository;

    @Test
    @DisplayName("회원이 가입한 크루 목록을 조회할 수 있다.")
    void findAllCrewsByMemberId_ReturnCrewProfileResponses() {
        // given
        final Long memberId = 1L;
        final Long loggedInMemberId = 1L;
        final MemberEntity member = buildMember();
        given(memberRepository.findById(anyLong())).willReturn(Optional.ofNullable(member));

        // when
        final List<CrewProfileResponse> crewProfileResponses = memberCrewService.findAllCrewsByMemberId(memberId,
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
        final MemberEntity member = buildMember();

        given(memberRepository.findById(anyLong())).willReturn(Optional.ofNullable(member));

        // when
        final List<CrewProfileResponse> crewProfileResponses = memberCrewService.findCreatedCrewsByMemberId(
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
        final MemberEntity member = buildMember();

        // when && then
        assertThatThrownBy(() -> memberCrewService.findCreatedCrewsByMemberId(
                loggedInMemberId,
                memberId
        )).isInstanceOf(MemberException.class)
                .hasMessage(MEMBER_MISMATCH.getMessage());
    }

    private MemberEntity buildMember() {
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
