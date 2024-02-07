package kr.pickple.back.member.service;

import static kr.pickple.back.common.domain.RegistrationStatus.*;
import static kr.pickple.back.member.exception.MemberExceptionCode.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.address.dto.response.MainAddress;
import kr.pickple.back.address.implement.AddressReader;
import kr.pickple.back.auth.config.property.JwtProperties;
import kr.pickple.back.auth.domain.token.AuthTokens;
import kr.pickple.back.auth.domain.token.JwtProvider;
import kr.pickple.back.auth.domain.token.RefreshToken;
import kr.pickple.back.auth.repository.RedisRepository;
import kr.pickple.back.crew.domain.Crew;
import kr.pickple.back.crew.domain.CrewMember;
import kr.pickple.back.crew.dto.response.CrewResponse;
import kr.pickple.back.crew.repository.CrewMemberRepository;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.domain.MemberPosition;
import kr.pickple.back.member.dto.request.MemberCreateRequest;
import kr.pickple.back.member.dto.response.AuthenticatedMemberResponse;
import kr.pickple.back.member.dto.response.MemberProfileResponse;
import kr.pickple.back.member.dto.response.MemberResponse;
import kr.pickple.back.member.exception.MemberException;
import kr.pickple.back.member.repository.MemberPositionRepository;
import kr.pickple.back.member.repository.MemberRepository;
import kr.pickple.back.position.domain.Position;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private static final String REFRESH_TOKEN_KEY = "refresh_token";

    private final AddressReader addressReader;

    private final MemberRepository memberRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final MemberPositionRepository memberPositionRepository;
    private final RedisRepository redisRepository;
    private final JwtProvider jwtProvider;
    private final JwtProperties jwtProperties;

    /**
     * 사용자 회원가입 (카카오)
     */
    @Transactional
    public AuthenticatedMemberResponse createMember(final MemberCreateRequest memberCreateRequest) {
        validateIsDuplicatedMemberInfo(memberCreateRequest);

        final MainAddress mainAddressId = addressReader.readMainAddressByNames(
                memberCreateRequest.getAddressDepth1(),
                memberCreateRequest.getAddressDepth2()
        );

        final Member member = memberCreateRequest.toEntity(mainAddressId);
        final Member savedMember = memberRepository.save(member);

        validatedIsDuplicatedPositions(memberCreateRequest.getPositions());
        final List<MemberPosition> memberPositions = memberCreateRequest.toMemberPositionEntities(savedMember);

        memberPositionRepository.saveAll(memberPositions); /* TODO: 벌크 연산으로 고치기 */

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

        final MainAddress mainAddress = addressReader.readMainAddressById(
                savedMember.getAddressDepth1Id(),
                savedMember.getAddressDepth2Id()
        );

        return AuthenticatedMemberResponse.of(savedMember, loginTokens, mainAddress);
    }

    private void validatedIsDuplicatedPositions(final List<Position> positions) {
        final Long distinctPositionsSize = positions.stream()
                .distinct()
                .count();

        if (distinctPositionsSize != positions.size()) {
            throw new MemberException(MEMBER_POSITIONS_IS_DUPLICATED, positions);
        }
    }

    private void validateIsDuplicatedMemberInfo(final MemberCreateRequest memberCreateRequest) {
        final String email = memberCreateRequest.getEmail();
        final String nickname = memberCreateRequest.getNickname();
        final Long oauthId = memberCreateRequest.getOauthId();

        if (memberRepository.existsByEmailOrNicknameOrOauthId(email, nickname, oauthId)) {
            throw new MemberException(MEMBER_IS_EXISTED, email, nickname, oauthId);
        }
    }

    /**
     * 사용자 프로필 조회
     */
    public MemberProfileResponse findMemberProfileById(final Long memberId) {
        final Member member = memberRepository.getMemberById(memberId);
        final List<Position> positions = getPositionsByMember(member);

        final List<Crew> crews = crewMemberRepository.findAllByMemberIdAndStatus(member.getId(), CONFIRMED)
                .stream()
                .map(CrewMember::getCrew)
                .toList();

        final List<CrewResponse> crewResponses = crews.stream()
                .map(crew -> CrewResponse.of(
                                crew,
                                getLeaderResponse(crew),
                                addressReader.readMainAddressById(crew.getAddressDepth1Id(), crew.getAddressDepth2Id())
                        )
                )
                .toList();

        final MainAddress mainAddress = addressReader.readMainAddressById(
                member.getAddressDepth1Id(),
                member.getAddressDepth2Id()
        );

        return MemberProfileResponse.of(member, crewResponses, positions, mainAddress);
    }

    private MemberResponse getLeaderResponse(final Crew crew) {
        final Member member = crew.getLeader();
        final MainAddress mainAddress = addressReader.readMainAddressById(
                member.getAddressDepth1Id(),
                member.getAddressDepth2Id()
        );

        return MemberResponse.of(member, getPositionsByMember(member), mainAddress);
    }

    private List<Position> getPositionsByMember(final Member member) {
        final List<MemberPosition> memberPositions = memberPositionRepository.findAllByMemberId(
                member.getId());

        return Position.fromMemberPositions(memberPositions);
    }
}
