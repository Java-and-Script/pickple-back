package kr.pickple.back.game.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.pickple.back.game.domain.GameMember;

public interface GameMemberRepository extends JpaRepository<GameMember, Long> {

}
