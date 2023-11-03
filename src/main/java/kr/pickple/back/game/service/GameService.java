package kr.pickple.back.game.service;

import static kr.pickple.back.game.exception.GameExceptionCode.*;
import static kr.pickple.back.member.exception.MemberExceptionCode.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.address.dto.response.MainAddressResponse;
import kr.pickple.back.address.service.AddressService;
import kr.pickple.back.common.domain.RegistrationStatus;
import kr.pickple.back.game.domain.Game;
import kr.pickple.back.game.domain.GameMember;
import kr.pickple.back.game.dto.request.GameCreateRequest;
import kr.pickple.back.game.dto.request.GameMemberCreateRequest;
import kr.pickple.back.game.dto.request.GameMemberRegistrationStatusUpdateRequest;
import kr.pickple.back.game.dto.response.GameIdResponse;
import kr.pickple.back.game.exception.GameException;
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
        final Member host = findMemberById(gameCreateRequest.getHostId());

        final MainAddressResponse mainAddressResponse = addressService.findMainAddressByAddressStrings(
                gameCreateRequest.getMainAddress());

        final Game game = gameCreateRequest.toEntity(mainAddressResponse, host);

        game.addGamePositions(gameCreateRequest.getPositions());

        final Game savedGame = gameRepository.save(game);

        final GameMember gameMember = GameMember.builder()
                .member(host)
                .game(savedGame)
                .build();

        gameMemberRepository.save(gameMember);

        return GameIdResponse.from(savedGame.getId());
    }

    @Transactional
    public void registerGameMember(final Long gameId, final GameMemberCreateRequest gameMemberCreateRequest) {
        final Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new GameException(GAME_NOT_FOUND, gameId));
        final Member member = findMemberById(gameMemberCreateRequest.getMemberId());

        game.addGameMember(member);
    }

    private Member findMemberById(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND, memberId));
    }

    @Transactional
    public void updateGameMemberRegistrationStatus(
            final Long gameId,
            final Long memberId,
            final GameMemberRegistrationStatusUpdateRequest gameMemberRegistrationStatusUpdateRequest
    ) {
        final GameMember gameMember = gameMemberRepository.findByMember_IdAndGame_Id(memberId, gameId)
                .orElseThrow();//TODO: ExceptionCode가 생기면 예외 추가 예정 (11.03 김영주)
        final RegistrationStatus newStatus = gameMemberRegistrationStatusUpdateRequest.getStatus();

        gameMember.updateStatus(newStatus);
    }
}
