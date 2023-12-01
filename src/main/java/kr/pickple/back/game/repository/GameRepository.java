package kr.pickple.back.game.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import kr.pickple.back.address.domain.AddressDepth1;
import kr.pickple.back.address.domain.AddressDepth2;
import kr.pickple.back.chat.domain.ChatRoom;
import kr.pickple.back.game.domain.Game;
import kr.pickple.back.game.domain.GameStatus;

public interface GameRepository extends JpaRepository<Game, Long>, GameSearchRepository {

    Page<Game> findByAddressDepth1AndAddressDepth2(
            final AddressDepth1 addressDepth1,
            final AddressDepth2 addressDepth2,
            final Pageable pageable
    );

    Optional<Game> findByChatRoom(final ChatRoom chatRoom);

    @Query("SELECT g FROM Game g WHERE g.status = :status AND CONCAT(g.playDate, ' ', g.playStartTime) <= DATE_FORMAT(:nowDateTime, '%Y-%m-%d %H:%i:%s')")
    List<Game> findGamesByStatusAndPlayDateStartTimeBeforeNow(final GameStatus status, final LocalDateTime nowDateTime);

    @Query("SELECT g FROM Game g WHERE g.status = :status AND CONCAT(g.playDate, ' ', g.playEndTime) <= DATE_FORMAT(:nowDateTime, '%Y-%m-%d %H:%i:%s')")
    List<Game> findGamesByStatusAndPlayDateEndTimeBeforeNow(final GameStatus status, final LocalDateTime nowDateTime);
}
