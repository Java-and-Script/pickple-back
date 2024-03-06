package kr.pickple.back.game.implement;

import kr.pickple.back.game.domain.GameMember;
import kr.pickple.back.game.repository.entity.GameMemberEntity;

public class GameMemberMapper {

    public static GameMemberEntity mapGameMemberDomainToEntity(final GameMember gameMember) {
        return GameMemberEntity.builder()
                .status(gameMember.getStatus())
                .memberId(gameMember.getMember().getMemberId())
                .gameId(gameMember.getGame().getGameId())
                .build();
    }
}
