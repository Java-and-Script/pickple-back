package kr.pickple.back.game.repository;

import static kr.pickple.back.game.exception.GameExceptionCode.*;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import kr.pickple.back.game.domain.GameStatus;
import kr.pickple.back.game.exception.GameException;
import kr.pickple.back.game.repository.entity.GameEntity;

public interface GameRepository extends JpaRepository<GameEntity, Long>, GameSearchRepository {

    Page<GameEntity> findByAddressDepth1IdAndAddressDepth2IdAndStatusNot(
            final Long addressDepth1Id,
            final Long addressDepth2Id,
            final GameStatus status,
            final Pageable pageable
    );

    Optional<GameEntity> findByChatRoomId(final Long chatRoomId);

    List<GameEntity> findAllByHostId(final Long hostId);

    @Query("update GameEntity g set g.memberCount = :memberCount, g.status = :status where g.id = :gameId")
    void updateMemberCountAndStatus(final Long gameId, final Integer memberCount, final GameStatus status);

    @Query("update GameEntity g set g.status = :status where g.id = :gameId")
    void updateRegistrationStatus(final GameStatus status, final Long gameId);

    @Query("select g.chatRoomId from GameEntity g where g.id = :gameId")
    Long findChatRoomId(final Long gameId);
}
