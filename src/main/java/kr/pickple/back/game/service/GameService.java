package kr.pickple.back.game.service;

import static kr.pickple.back.member.exception.MemberExceptionCode.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.address.dto.response.MainAddressResponse;
import kr.pickple.back.address.service.AddressService;
import kr.pickple.back.common.domain.RegistrationStatus;
import kr.pickple.back.game.domain.Game;
import kr.pickple.back.game.domain.GameMember;
import kr.pickple.back.game.dto.request.GameCreateRequest;
import kr.pickple.back.game.dto.response.GameIdResponse;
import kr.pickple.back.game.repository.GameMemberRepository;
import kr.pickple.back.game.repository.GameRepository;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.exception.MemberException;
import kr.pickple.back.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GameService {

    private final AddressService addressService;
    private final GameRepository gameRepository;
    private final GameMemberRepository gameMemberRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public GameIdResponse createGame(final GameCreateRequest gameCreateRequest) {
        final Member host = memberRepository.findById(gameCreateRequest.getHostId())
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND, gameCreateRequest.getHostId()));

        final MainAddressResponse mainAddressResponse = addressService.findMainAddressByAddressStrings(
                gameCreateRequest.getMainAddress());

        final Game game = gameCreateRequest.toEntity(mainAddressResponse, host);
        game.addGamePositions(gameCreateRequest.getPositions());
        final Game savedGame = gameRepository.save(game);

        final GameMember gameMember = GameMember.builder()
                .status(RegistrationStatus.CONFIRMED)
                .member(host)
                .game(savedGame)
                .build();

        gameMemberRepository.save(gameMember);

        return GameIdResponse.from(savedGame.getId());
    }
}