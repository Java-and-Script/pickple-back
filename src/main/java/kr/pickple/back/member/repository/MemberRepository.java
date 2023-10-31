package kr.pickple.back.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.pickple.back.member.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Boolean existsByEmailOrNicknameOrOauthId(final String email, final String nickname, final Long oauthId);
}
