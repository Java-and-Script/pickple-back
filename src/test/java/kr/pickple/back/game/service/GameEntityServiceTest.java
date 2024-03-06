package kr.pickple.back.game.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.pickple.back.address.domain.AddressDepth1;
import kr.pickple.back.address.domain.AddressDepth2;
import kr.pickple.back.fixture.domain.GameFixtures;
import kr.pickple.back.fixture.domain.MemberFixtures;
import kr.pickple.back.game.repository.entity.GameEntity;
import kr.pickple.back.game.dto.response.GameResponse;
import kr.pickple.back.game.repository.GameRepository;
import kr.pickple.back.member.domain.Member;

@ExtendWith(MockitoExtension.class)
public class GameEntityServiceTest {

    @InjectMocks
    private GameService gameService;

    @Mock
    private GameRepository gameRepository;

    @Test
    @DisplayName("게스트 모집을 상세 조회할 수 있다.")
    void findGameById_ReturnGameResponse() {
        // given
        final AddressDepth1 addressDepth1 = AddressDepth1.builder()
                .name("서울시")
                .build();
        final AddressDepth2 addressDepth2 = AddressDepth2.builder()
                .name("영등포구")
                .addressDepth1(addressDepth1)
                .build();

        final Member host = MemberFixtures.memberBuild(addressDepth1, addressDepth2);

        final GameEntity gameEntity = GameFixtures.gameBuild(addressDepth1, addressDepth2, host);

        given(gameRepository.findById(anyLong())).willReturn(Optional.ofNullable(gameEntity));

        // when
        final GameResponse gameResponse = gameService.findGameById(1L);

        // then
        assertThat(gameResponse).isNotNull();
    }
}
