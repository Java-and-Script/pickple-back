package kr.pickple.back.member.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.domain.MemberPosition;

public interface MemberPositionRepository extends JpaRepository<MemberPosition, Long> {

    List<MemberPosition> findAllByMember(final Member member);
}
