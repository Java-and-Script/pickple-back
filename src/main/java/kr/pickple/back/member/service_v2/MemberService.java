package kr.pickple.back.member.service_v2;

import static kr.pickple.back.common.domain.RegistrationStatus.*;
import static kr.pickple.back.member.exception.MemberExceptionCode.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.address.dto.response.MainAddressResponse;
import kr.pickple.back.address.service.AddressService;
import kr.pickple.back.auth.config.property.JwtProperties;
import kr.pickple.back.auth.domain.token.AuthTokens;
import kr.pickple.back.auth.domain.token.JwtProvider;
import kr.pickple.back.auth.domain.token.RefreshToken;
import kr.pickple.back.auth.repository.RedisRepository;
import kr.pickple.back.crew.dto.response.CrewResponse;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.dto.request.MemberCreateRequest;
import kr.pickple.back.member.dto.response.AuthenticatedMemberResponse;
import kr.pickple.back.member.dto.response.MemberProfileResponse;
import kr.pickple.back.member.exception.MemberException;
import kr.pickple.back.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private static final String REFRESH_TOKEN_KEY = "refresh_token";

    private final AddressService addressService;
    private final MemberRepository memberRepository;
    private final RedisRepository redisRepository;
    private final JwtProvider jwtProvider;
    private final JwtProperties jwtProperties;

    /**
     * 사용자 회원가입 (카카오)
     */
    @Transactional
    public AuthenticatedMemberResponse createMember(final MemberCreateRequest memberCreateRequest) {
        validateIsDuplicatedMemberInfo(memberCreateRequest);

        final MainAddressResponse mainAddressResponse = addressService.findMainAddressByNames(
                memberCreateRequest.getAddressDepth1(),
                memberCreateRequest.getAddressDepth2()
        );

        final Member member = memberCreateRequest.toEntity(mainAddressResponse);
        final Member savedMember = memberRepository.save(member);

        final AuthTokens loginTokens = jwtProvider.createLoginToken(String.valueOf(savedMember.getId()));

        final RefreshToken refreshToken = RefreshToken.builder()
                .token(loginTokens.getRefreshToken())
                .memberId(savedMember.getId())
                .createdAt(LocalDateTime.now())
                .build();

        redisRepository.saveHash(
                REFRESH_TOKEN_KEY,
                refreshToken.getToken(),
                refreshToken,
                jwtProperties.getRefreshTokenExpirationTime()
        );

        return AuthenticatedMemberResponse.of(savedMember, loginTokens);
    }

    /**
     * 사용자 프로필 조회
     */
    public MemberProfileResponse findMemberProfileById(final Long memberId) {
        final Member member = memberRepository.getMemberById(memberId);
        final List<CrewResponse> crewResponses = member.getCrewsByStatus(CONFIRMED)
                .stream()
                .map(CrewResponse::from)
                .toList();

        return MemberProfileResponse.of(member, crewResponses);
    }

    private void validateIsDuplicatedMemberInfo(final MemberCreateRequest memberCreateRequest) {
        final String email = memberCreateRequest.getEmail();
        final String nickname = memberCreateRequest.getNickname();
        final Long oauthId = memberCreateRequest.getOauthId();

        if (memberRepository.existsByEmailOrNicknameOrOauthId(email, nickname, oauthId)) {
            throw new MemberException(MEMBER_IS_EXISTED, email, nickname, oauthId);
        }
    }
}
