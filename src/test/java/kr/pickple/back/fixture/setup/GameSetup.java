package kr.pickple.back.fixture.setup;

import static kr.pickple.back.common.domain.RegistrationStatus.*;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import kr.pickple.back.address.domain.AddressDepth1;
import kr.pickple.back.address.domain.AddressDepth2;
import kr.pickple.back.chat.domain.ChatRoom;
import kr.pickple.back.chat.repository.ChatRoomRepository;
import kr.pickple.back.fixture.domain.GameFixtures;
import kr.pickple.back.game.repository.GameRepository;
import kr.pickple.back.game.repository.entity.GameEntity;
import kr.pickple.back.game.repository.entity.GameMemberEntity;
import kr.pickple.back.member.repository.entity.MemberEntity;

@Component
public class GameSetup {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private MemberSetup memberSetup;

    @Autowired
    private AddressSetup addressSetup;

    public GameEntity save(final MemberEntity host) {
        final AddressDepth1 addressDepth1 = addressSetup.findAddressDepth1("서울시");
        final AddressDepth2 addressDepth2 = addressSetup.findAddressDepth2("영등포구");

        final GameEntity gameEntity = GameFixtures.gameBuild(addressDepth1, addressDepth2, host);
        final ChatRoom savedChatRoom = chatRoomRepository.save(GameFixtures.gameChatRoomBuild());

        gameEntity.addGameMember(host);
        savedChatRoom.updateMaxMemberCount(gameEntity.getMaxMemberCount());
        gameEntity.makeNewGameChatRoom(savedChatRoom);

        final GameMemberEntity gameHost = gameEntity.getGameMembers().get(0);
        host.addMemberGame(gameHost);
        gameHost.updateStatus(CONFIRMED);

        return gameRepository.save(gameEntity);
    }

    public GameEntity saveWithWaitingMembers(final Integer memberCount) {
        final List<MemberEntity> members = memberSetup.save(memberCount);
        final GameEntity gameEntity = save(members.get(0));
        final List<MemberEntity> guests = members.subList(1, members.size());

        guests.forEach(gameEntity::addGameMember);

        return gameEntity;
    }

    public GameEntity saveWithConfirmedMembers(final Integer memberCount) {
        final GameEntity gameEntity = saveWithWaitingMembers(memberCount);
        final MemberEntity host = gameEntity.getHost();
        final List<GameMemberEntity> gameMemberEntities = gameEntity.getGameMembers();

        gameMemberEntities.forEach(gameMember -> {
            if (!host.equals(gameMember.getMember())) {
                gameMember.updateStatus(CONFIRMED);
            }
        });

        return gameEntity;
    }
}
