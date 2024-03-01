package kr.pickple.back.member.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.auth.domain.token.AuthTokens;
import kr.pickple.back.auth.implement.TokenManager;
import kr.pickple.back.member.domain.MemberProfile;
import kr.pickple.back.member.domain.NewMember;
import kr.pickple.back.member.dto.mapper.MemberResponseMapper;
import kr.pickple.back.member.dto.response.AuthenticatedMemberResponse;
import kr.pickple.back.member.dto.response.MemberProfileResponse;
import kr.pickple.back.member.implement.MemberReader;
import kr.pickple.back.member.implement.MemberWriter;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final TokenManager tokenManager;
    private final MemberWriter memberWriter;
    private final MemberReader memberReader;

    /**
     * 사용자 회원가입 (카카오)
     */
    @Transactional
    public AuthenticatedMemberResponse createMember(final NewMember newMember) {
        final NewMember savedNewMember = memberWriter.create(newMember);
        final AuthTokens authTokens = tokenManager.create(savedNewMember.getMemberId());
        savedNewMember.updateAuthTokens(authTokens);

        return MemberResponseMapper.mapToAuthenticatedMemberResponseDto(savedNewMember);
    }

    /**
     * 사용자 프로필 조회
     */
    public MemberProfileResponse findMemberProfileById(final Long memberId) {
        final MemberProfile memberProfile = memberReader.readProfileByMemberId(memberId);

        return MemberResponseMapper.mapToMemberProfileResponseDto(memberProfile);
    }
}
