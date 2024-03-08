package kr.pickple.back.member.service;

import static kr.pickple.back.common.domain.RegistrationStatus.*;
import static kr.pickple.back.member.exception.MemberExceptionCode.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.address.implement.AddressReader;
import kr.pickple.back.common.domain.RegistrationStatus;
import kr.pickple.back.game.repository.entity.GameEntity;
import kr.pickple.back.game.repository.entity.GameMemberEntity;
import kr.pickple.back.game.repository.GameMemberRepository;
import kr.pickple.back.game.repository.GameRepository;
import kr.pickple.back.member.repository.entity.MemberEntity;
import kr.pickple.back.member.repository.entity.MemberPositionEntity;
import kr.pickple.back.member.dto.response.GameMemberRegistrationStatusResponse;
import kr.pickple.back.member.dto.response.MemberGameResponse;
import kr.pickple.back.member.dto.response.MemberResponse;
import kr.pickple.back.member.exception.MemberException;
import kr.pickple.back.member.implement.MemberReader;
import kr.pickple.back.position.domain.Position;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberGameService {

    private final AddressReader addressReader;
    private final MemberReader memberReader;
    private final GameRepository gameRepository;
    private final GameMemberRepository gameMemberRepository;

    /**
     * 사용자의 참여 확정 게스트 모집글 목록 조회
     */
    public List<MemberGameResponse> findAllMemberGames(
            final Long memberId,
            final RegistrationStatus memberStatus
    ) {
        final MemberEntity member = memberReader.readEntityByMemberId(memberId);
        final List<GameMemberEntity> memberGames = gameMemberRepository.findAllByMemberIdAndStatus(member.getId(),
                memberStatus);

        return convertToMemberGameResponses(memberGames, memberStatus);
    }

    /**
     * 사용자가 만든 게스트 모집글 목록 조회
     */
    public List<MemberGameResponse> findAllCreatedGames(final Long memberId) {
        final MemberEntity member = memberReader.readEntityByMemberId(memberId);
        final List<GameMemberEntity> memberGames = gameMemberRepository.findAllByMemberId(member.getId());

        return convertToMemberGameResponses(memberGames, CONFIRMED);
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

        final GameMemberEntity gameMemberEntity = gameMemberRepository.findByMemberIdAndGameId(member.getId(), gameEntity.getId())
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND, member.getId(), gameEntity.getId()));

        return GameMemberRegistrationStatusResponse.of(gameMemberEntity.getStatus(), gameMemberEntity.isAlreadyReviewDone());
    }

    private List<MemberGameResponse> convertToMemberGameResponses(
            final List<GameMemberEntity> memberGames,
            final RegistrationStatus memberStatus
    ) {
        return memberGames.stream()
                .map(memberGame -> {
                    GameEntity gameEntity = gameRepository.getGameById(memberGame.getGameId());
                    MemberEntity member = memberRepository.getMemberById(memberGame.getMemberId());

                    return MemberGameResponse.of(
                            memberGame,
                            gameEntity,
                            getMemberResponsesByGame(gameEntity, memberStatus),
                            getPositionsByMember(member),
                            addressReader.readMainAddressById(
                                    gameEntity.getAddressDepth1Id(),
                                    gameEntity.getAddressDepth2Id()
                            )
                    );
                })
                .toList();
    }

    private List<MemberResponse> getMemberResponsesByGame(final GameEntity gameEntity, final RegistrationStatus memberStatus) {
        return gameMemberRepository.findAllByGameIdAndStatus(gameEntity.getId(), memberStatus)
                .stream()
                .map(gameMember -> memberRepository.getMemberById(gameMember.getMemberId()))
                .map(member -> MemberResponse.of(
                                member,
                                getPositionsByMember(member),
                                addressReader.readMainAddressById(member.getAddressDepth1Id(), member.getAddressDepth2Id())
                        )
                )
                .toList();
    }

    private List<Position> getPositionsByMember(final MemberEntity member) {
        final List<MemberPositionEntity> memberPositions = memberPositionReader.readAll(member.getId());

        return Position.fromMemberPositions(memberPositions);
    }
}
