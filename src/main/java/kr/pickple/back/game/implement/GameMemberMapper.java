package kr.pickple.back.game.implement;

import kr.pickple.back.game.domain.Game;
import kr.pickple.back.game.domain.GameMember;
import kr.pickple.back.game.repository.entity.GameMemberEntity;
import kr.pickple.back.member.domain.Member;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GameMemberMapper {

    public static GameMemberEntity mapGameMemberDomainToEntity(final GameMember gameMember) {
        return GameMemberEntity.builder()
                .status(gameMember.getStatus())
                .memberId(gameMember.getMember().getMemberId())
                .gameId(gameMember.getGame().getGameId())
                .build();
    }

    public static GameMember mapGameMemberEntityToDomain(
            final GameMemberEntity gameMemberEntity,
            final Member member,
            final Game game
    ) {
        return GameMember.builder()
                .gameMemberId(gameMemberEntity.getId())
                .status(gameMemberEntity.getStatus())
                .member(member)
                .game(game)
                .isReview(gameMemberEntity.isReviewDone())
                .build();
    }
}
