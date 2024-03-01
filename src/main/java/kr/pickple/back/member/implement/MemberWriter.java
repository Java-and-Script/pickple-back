package kr.pickple.back.member.implement;

import static kr.pickple.back.member.exception.MemberExceptionCode.*;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.address.dto.response.MainAddress;
import kr.pickple.back.address.implement.AddressReader;
import kr.pickple.back.auth.implement.TokenManager;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.domain.MemberPosition;
import kr.pickple.back.member.domain.NewMember;
import kr.pickple.back.member.exception.MemberException;
import kr.pickple.back.member.repository.MemberPositionRepository;
import kr.pickple.back.member.repository.MemberRepository;
import kr.pickple.back.position.domain.Position;
import lombok.RequiredArgsConstructor;

@Component
@Transactional
@RequiredArgsConstructor
public class MemberWriter {

    private final TokenManager tokenManager;
    private final AddressReader addressReader;
    private final MemberRepository memberRepository;
    private final MemberPositionRepository memberPositionRepository;

    public NewMember create(final NewMember newMember) {
        validateIsDuplicatedMemberInfo(newMember);

        final MainAddress mainAddress = addressReader.readMainAddressByNames(
                newMember.getAddressDepth1Name(),
                newMember.getAddressDepth2Name()
        );

        final Member memberEntity = MemberMapper.mapToMemberEntity(newMember, mainAddress);
        final Member savedMemberEntity = memberRepository.save(memberEntity);

        newMember.updateMemberId(savedMemberEntity.getId());
        setPositionsToMember(newMember.getPositions(), newMember.getMemberId());

        return newMember;
    }

    private void setPositionsToMember(final List<Position> positions, final Long memberId) {
        validateIsDuplicatedPositions(positions);

        final List<MemberPosition> memberPositions = MemberMapper.mapToMemberPositionEntities(positions, memberId);

        memberPositionRepository.saveAll(memberPositions);
    }

    private void validateIsDuplicatedMemberInfo(final NewMember newMember) {
        final String email = newMember.getEmail();
        final String nickname = newMember.getNickname();
        final Long oauthId = newMember.getOauthId();

        if (memberRepository.existsByEmailOrNicknameOrOauthId(email, nickname, oauthId)) {
            throw new MemberException(MEMBER_IS_EXISTED, email, nickname, oauthId);
        }
    }

    private void validateIsDuplicatedPositions(final List<Position> positions) {
        final Long distinctPositionsSize = positions.stream()
                .distinct()
                .count();

        if (distinctPositionsSize != positions.size()) {
            throw new MemberException(MEMBER_POSITIONS_IS_DUPLICATED, positions);
        }
    }
}
