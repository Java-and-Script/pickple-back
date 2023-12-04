package kr.pickple.back.game.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import kr.pickple.back.address.domain.AddressDepth1;
import kr.pickple.back.address.domain.AddressDepth2;
import kr.pickple.back.chat.domain.ChatRoom;
import kr.pickple.back.game.domain.Game;
import kr.pickple.back.game.domain.GameStatus;

public interface GameRepository extends JpaRepository<Game, Long>, GameSearchRepository {

    Page<Game> findByAddressDepth1AndAddressDepth2AndStatusNot(
            final AddressDepth1 addressDepth1,
            final AddressDepth2 addressDepth2,
            final GameStatus status,
            final Pageable pageable
    );

    Optional<Game> findByChatRoom(final ChatRoom chatRoom);
}
