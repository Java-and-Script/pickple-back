package kr.pickple.back.member.repository;

import static kr.pickple.back.member.exception.MemberExceptionCode.*;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.exception.MemberException;

public interface MemberRepository extends JpaRepository<Member, Long> {

	Boolean existsByEmailOrNicknameOrOauthId(final String email, final String nickname, final Long oauthId);

	Optional<Member> findByOauthId(final Long oauthId);

	default Member getMemberById(final Long memberId) {
		return findById(memberId).orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND, memberId));
	}
}
