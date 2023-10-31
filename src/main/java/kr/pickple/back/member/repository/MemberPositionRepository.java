package kr.pickple.back.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.pickple.back.member.domain.MemberPosition;

public interface MemberPositionRepository extends JpaRepository<MemberPosition, Long> {

}
