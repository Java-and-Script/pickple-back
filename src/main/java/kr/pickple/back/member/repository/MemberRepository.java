package kr.pickple.back.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import kr.pickple.back.member.repository.entity.MemberEntity;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

    Boolean existsByEmailOrNicknameOrOauthId(final String email, final String nickname, final Long oauthId);

    Optional<MemberEntity> findByOauthId(final Long oauthId);

    @Query("""
            update MemberEntity m 
            set m.mannerScore = :mannerScore, m.mannerScoreCount = :mannerScoreCount 
            where m.id = :memberId""")
    void updateMannerScore(final Long memberId, final Integer mannerScore, final Integer mannerScoreCount);
}
