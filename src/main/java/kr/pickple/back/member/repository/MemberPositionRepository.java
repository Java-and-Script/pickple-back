package kr.pickple.back.member.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.pickple.back.member.repository.entity.MemberPositionEntity;

public interface MemberPositionRepository extends JpaRepository<MemberPositionEntity, Long> {

    List<MemberPositionEntity> findAllByMemberId(final Long memberId);
}
