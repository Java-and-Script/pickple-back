package kr.pickple.back.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.pickple.back.member.repository.entity.MemberEntity;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

    Boolean existsByEmailOrNicknameOrOauthId(final String email, final String nickname, final Long oauthId);

    Optional<MemberEntity> findByOauthId(final Long oauthId);

    void updateMannerScore(Integer mannerScore, Long memberId);
}
