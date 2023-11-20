package kr.pickple.back.member.repository;

import kr.pickple.back.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Boolean existsByEmailOrNicknameOrOauthId(final String email, final String nickname, final Long oauthId);

    Optional<Member> findByOauthId(final Long oauthId);
}
