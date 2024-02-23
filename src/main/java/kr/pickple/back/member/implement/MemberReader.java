package kr.pickple.back.member.implement;

import static kr.pickple.back.member.exception.MemberExceptionCode.*;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.address.dto.response.MainAddress;
import kr.pickple.back.address.implement.AddressReader;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.domain.MemberDomain;
import kr.pickple.back.member.domain.MemberPosition;
import kr.pickple.back.member.domain.MemberProfile;
import kr.pickple.back.member.exception.MemberException;
import kr.pickple.back.member.mapper.MemberMapper;
import kr.pickple.back.member.repository.MemberPositionRepository;
import kr.pickple.back.member.repository.MemberRepository;
import kr.pickple.back.position.domain.Position;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberReader {

    private final AddressReader addressReader;
    private final MemberMapper memberMapper;
    private final MemberRepository memberRepository;
    private final MemberPositionRepository memberPositionRepository;

    public MemberProfile readProfileByMemberId(final Long memberId) {
        final Member member = readEntityByMemberId(memberId);
        final MainAddress mainAddress = addressReader.readMainAddressById(
                member.getAddressDepth1Id(),
                member.getAddressDepth2Id()
        );
        final List<Position> positions = readPositionsByMemberId(memberId);

        return MemberMapper.mapToMemberProfileDomain(member, mainAddress, positions);
    }

    //TODO: member -> memberDomain 변경 작업 후 제거 예정 (김영주)
    public Member readEntityByMemberId(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND, memberId));
    }

    public MemberDomain readByMemberId(final Long memberId) {
        final Member memberEntity = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND, memberId));

        return memberMapper.mapToMemberDomain(memberEntity);
    }

    public List<Position> readPositionsByMemberId(final Long memberId) {
        return memberPositionRepository.findAllByMemberId(memberId)
                .stream()
                .map(MemberPosition::getPosition)
                .toList();
    }

    public Optional<Member> readByOauthId(final Long oauthId) {
        return memberRepository.findByOauthId(oauthId);
    }
}
