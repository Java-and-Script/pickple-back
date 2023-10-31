package kr.pickple.back.member.service;

import static kr.pickple.back.member.exception.MemberExceptionCode.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.address.dto.response.MainAddressResponse;
import kr.pickple.back.address.service.AddressService;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.domain.MemberPosition;
import kr.pickple.back.member.dto.request.MemberCreateRequest;
import kr.pickple.back.member.dto.response.AuthenticatedMemberResponse;
import kr.pickple.back.member.exception.MemberException;
import kr.pickple.back.member.repository.MemberPositionRepository;
import kr.pickple.back.member.repository.MemberRepository;
import kr.pickple.back.position.domain.Position;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final AddressService addressService;
    private final MemberRepository memberRepository;
    private final MemberPositionRepository memberPositionRepository;

    @Transactional
    public AuthenticatedMemberResponse createMember(final MemberCreateRequest memberCreateRequest) {
        validateIsDuplicatedMemberInfo(
                memberCreateRequest.getEmail(),
                memberCreateRequest.getNickname(),
                memberCreateRequest.getOauthId()
        );

        final MainAddressResponse mainAddressResponse = addressService.findMainAddressByNames(
                memberCreateRequest.getAddressDepth1(),
                memberCreateRequest.getAddressDepth2()
        );

        final Member member = memberCreateRequest.toEntity(mainAddressResponse);
        final Member savedMember = memberRepository.save(member);

        final List<MemberPosition> positions = memberCreateRequest.getPositions()
                .stream()
                .map(position -> MemberPosition.builder()
                        .position(Position.from(position))
                        .member(savedMember)
                        .build())
                .toList();

        memberPositionRepository.saveAll(positions);

        return AuthenticatedMemberResponse.from(savedMember);
    }

    private void validateIsDuplicatedMemberInfo(final String email, final String nickname, final Long oauthId) {
        if (memberRepository.existsByEmailOrNicknameOrOauthId(email, nickname, oauthId)) {
            throw new MemberException(MEMBER_IS_EXISTED, email, nickname, oauthId);
        }
    }
}
