package kr.pickple.back.game.implement;

import kr.pickple.back.game.domain.GameDomain;
import kr.pickple.back.game.domain.GameMemberDomain;
import kr.pickple.back.game.repository.entity.GameMemberEntity;
import kr.pickple.back.member.domain.MemberDomain;

public class GameMemberMapper {

    public static GameMemberEntity mapGameMemberDomainToEntity(final GameMemberDomain gameMemberDomain) {
        return GameMemberEntity.builder()
                .status(gameMemberDomain.getStatus())
                .memberId(gameMemberDomain.getMember().getMemberId())
                .gameId(gameMemberDomain.getGame().getGameId())
                .build();
    }

    public static GameMemberDomain mapGameMemberEntityToDomain(GameMemberEntity gameMemberEntity, MemberDomain memberDomain, GameDomain gameDomain) {
        return GameMemberDomain.builder()
                .gameMemberId(gameMemberEntity.getId())
                .status(gameMemberEntity.getStatus())
                .member(memberDomain)
                .game(gameDomain)
                .build();
    }
}
