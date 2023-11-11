package kr.pickple.back.member.domain;

import static kr.pickple.back.member.exception.MemberExceptionCode.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import kr.pickple.back.address.domain.AddressDepth1;
import kr.pickple.back.address.domain.AddressDepth2;
import kr.pickple.back.fixture.domain.AddressFixtures;
import kr.pickple.back.fixture.domain.MemberFixtures;
import kr.pickple.back.member.exception.MemberException;

class MemberTest {

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 1})
    @DisplayName("회원의 매너스코어를 업데이트 시킬 수 있다.")
    void updateMannerScore(int mannerScorePoint) {
        // given
        final AddressDepth1 addressDepth1 = AddressFixtures.addressDepth1Build();
        final AddressDepth2 addressDepth2 = AddressFixtures.addressDepth2Build();

        final Member member = MemberFixtures.memberBuild(addressDepth1, addressDepth2);

        // when
        member.updateMannerScore(mannerScorePoint);

        // then
        assertThat(member.getMannerScore()).isEqualTo(mannerScorePoint);
    }

    @ParameterizedTest
    @ValueSource(ints = {-300, -2, 10, 10000})
    @DisplayName("회원의 매너스코어를 업데이트할 때 매너스코어 포인트가 범위에서 벗어난 경우 예외가 발생한다.")
    void updateMannerScore_ThrowException(int mannerScorePoint) {
        // given
        final AddressDepth1 addressDepth1 = AddressFixtures.addressDepth1Build();
        final AddressDepth2 addressDepth2 = AddressFixtures.addressDepth2Build();

        final Member member = MemberFixtures.memberBuild(addressDepth1, addressDepth2);

        // when & then
        assertThatThrownBy(() -> member.updateMannerScore(mannerScorePoint))
                .isInstanceOf(MemberException.class)
                .hasMessage(MEMBER_UPDATING_MANNER_SCORE_POINT_OUT_OF_RANGE.getMessage());
    }
}
