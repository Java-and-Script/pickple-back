package kr.pickple.back.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.pickple.back.auth.domain.token.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

}
