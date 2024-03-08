package kr.pickple.back.member.service;

import static kr.pickple.back.common.domain.RegistrationStatus.*;
import static kr.pickple.back.member.exception.MemberExceptionCode.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.common.domain.RegistrationStatus;
import kr.pickple.back.game.domain.Game;
import kr.pickple.back.game.implement.GameMemberReader;
import kr.pickple.back.game.implement.GameReader;
import kr.pickple.back.game.repository.entity.GameEntity;
import kr.pickple.back.game.repository.entity.GameMemberEntity;
import kr.pickple.back.member.dto.mapper.MemberResponseMapper;
import kr.pickple.back.member.dto.response.GameMemberRegistrationStatusResponse;
import kr.pickple.back.member.dto.response.MemberGameResponse;
import kr.pickple.back.member.exception.MemberException;
import kr.pickple.back.member.implement.MemberReader;
import kr.pickple.back.member.repository.entity.MemberEntity;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberGameService {

    private final MemberReader memberReader;
    private final GameReader gameReader;
    private final GameMemberReader gameMemberReader;

    /**
     * 사용자의 참여 확정 게스트 모집글 목록 조회
     */
    public List<MemberGameResponse> findAllMemberGames(final Long memberId, final RegistrationStatus status) {
        return gameMemberReader.readAllByMemberIdAndStatus(memberId, status)
                .stream()
                .map(memberGame -> MemberResponseMapper.mapToMemberGameResponseDto(
                                memberGame.getGame(),
                                gameMemberReader.readMembersByGameIdAndStatus(memberGame.getGame().getGameId(), CONFIRMED),
                                memberGame.isReviewDone()
                        )
                ).toList();
    }

    /**
     * 사용자가 만든 게스트 모집글 목록 조회
     */
    public List<MemberGameResponse> findAllCreatedGames(final Long hostId) {
        final List<Game> createdGames = gameReader.readAllByHostId(hostId);

        return createdGames.stream()
                .map(game -> MemberResponseMapper.mapToMemberGameResponseDto(
                                game,
                                gameMemberReader.readMembersByGameIdAndStatus(game.getGameId(), CONFIRMED),
                                gameMemberReader.isReviewDoneByGameIdAndMemberId(game.getGameId(), hostId)
                        )
                ).toList();
    }

    /**
     * 회원의 게스트 모집 신청 여부 조회
     */
    public GameMemberRegistrationStatusResponse findMemberRegistrationStatusForGame(
            final Long memberId,
            final Long gameId
    ) {
        final MemberEntity member = memberReader.readEntityByMemberId(memberId);
        final GameEntity gameEntity = gameRepository.getGameById(gameId);

        final GameMemberEntity gameMemberEntity = gameMemberRepository.findByMemberIdAndGameId(member.getId(),
                        gameEntity.getId())
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND, member.getId(), gameEntity.getId()));

        return GameMemberRegistrationStatusResponse.of(gameMemberEntity.getStatus(), gameMemberEntity.isReviewDone());
    }
}
