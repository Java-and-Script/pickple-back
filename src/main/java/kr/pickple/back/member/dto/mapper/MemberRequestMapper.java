package kr.pickple.back.member.dto.mapper;

import kr.pickple.back.member.domain.NewMember;
import kr.pickple.back.member.dto.request.MemberCreateRequest;

public final class MemberRequestMapper {

    public static NewMember mapToNewMemberDomain(final MemberCreateRequest memberCreateRequest) {
        return NewMember.builder()
                .email(memberCreateRequest.getEmail())
                .nickname(memberCreateRequest.getNickname())
                .profileImageUrl(memberCreateRequest.getProfileImageUrl())
                .positions(memberCreateRequest.getPositions())
                .oauthId(memberCreateRequest.getOauthId())
                .oauthProvider(memberCreateRequest.getOauthProvider())
                .addressDepth1Name(memberCreateRequest.getAddressDepth1())
                .addressDepth2Name(memberCreateRequest.getAddressDepth2())
                .build();
    }
}
