package kr.pickple.back.game.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import kr.pickple.back.common.domain.RegistrationStatus;
import kr.pickple.back.game.repository.entity.GameMemberEntity;

public interface GameMemberRepository extends JpaRepository<GameMemberEntity, Long> {

    Optional<GameMemberEntity> findByMemberIdAndGameId(final Long memberId, final Long gameId);

    Optional<GameMemberEntity> findByMemberIdAndGameIdAndStatus(
            final Long memberId,
            final Long gameId,
            final RegistrationStatus status
    );

    List<GameMemberEntity> findAllByMemberIdAndStatus(final Long memberId, final RegistrationStatus memberStatus);

    List<GameMemberEntity> findAllByMemberId(final Long memberId);

    List<GameMemberEntity> findAllByGameIdAndStatus(final Long gameId, final RegistrationStatus status);

    Boolean existsByGameIdAndMemberId(final Long gameId, final Long memberId);

    @Query("update GameMemberEntity gm set gm.status = :status where gm.id = :gameMemberId")
    void updateRegistrationStatus(final Long gameMemberId, final RegistrationStatus status);
}
