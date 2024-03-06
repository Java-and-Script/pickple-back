package kr.pickple.back.game.implement;

import kr.pickple.back.game.domain.GameDomain;
import kr.pickple.back.game.domain.GameMember;
import kr.pickple.back.game.repository.entity.GameMemberEntity;
import kr.pickple.back.member.domain.MemberDomain;

public class GameMemberMapper {

    public static GameMemberEntity mapGameMemberDomainToEntity(final GameMember gameMember) {
        return GameMemberEntity.builder()
                .status(gameMember.getStatus())
                .memberId(gameMember.getMember().getMemberId())
                .gameId(gameMember.getGame().getGameId())
                .build();
    }

    public static GameMember mapGameMemberEntityToDomain(GameMemberEntity gameMemberEntity, MemberDomain memberDomain, GameDomain gameDomain) {
        return GameMember.builder()
                .gameMemberId(gameMemberEntity.getId())
                .status(gameMemberEntity.getStatus())
                .member(memberDomain)
                .game(gameDomain)
                .build();
    }
}
