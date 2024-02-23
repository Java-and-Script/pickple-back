package kr.pickple.back.member.mapper;

import static kr.pickple.back.common.domain.RegistrationStatus.*;

import java.util.List;

import org.springframework.stereotype.Component;

import kr.pickple.back.address.dto.response.MainAddress;
import kr.pickple.back.address.implement.AddressReader;
import kr.pickple.back.crew.implement.CrewReader;
import kr.pickple.back.game.domain.Game;
import kr.pickple.back.game.repository.GameMemberRepository;
import kr.pickple.back.game.repository.GameRepository;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.domain.MemberDomain;
import kr.pickple.back.member.domain.MemberPosition;
import kr.pickple.back.member.domain.MemberProfile;
import kr.pickple.back.member.domain.MemberStatus;
import kr.pickple.back.member.domain.NewMember;
import kr.pickple.back.member.repository.MemberPositionRepository;
import kr.pickple.back.position.domain.Position;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MemberMapper {

    private final AddressReader addressReader;
    private final CrewReader crewReader;
    private final MemberPositionRepository memberPositionRepository;
    private final GameRepository gameRepository;
    private final GameMemberRepository gameMemberRepository;

    public static Member mapToMemberEntity(final NewMember newMember, final MainAddress mainAddress) {
        return Member.builder()
                .email(newMember.getEmail())
                .nickname(newMember.getNickname())
                .profileImageUrl(newMember.getProfileImageUrl())
                .status(MemberStatus.ACTIVE)
                .oauthId(newMember.getOauthId())
                .oauthProvider(newMember.getOauthProvider())
                .addressDepth1Id(mainAddress.getAddressDepth1().getId())
                .addressDepth2Id(mainAddress.getAddressDepth2().getId())
                .build();
    }

    public static List<MemberPosition> mapToMemberPositionEntities(
            final List<Position> positions,
            final Long memberId
    ) {
        return positions.stream()
                .map(position -> MemberPosition.builder()
                        .memberId(memberId)
                        .position(position)
                        .build()
                ).toList();
    }

    public static MemberProfile mapToMemberProfileDomain(
            final Member member,
            final MainAddress mainAddress,
            final List<Position> positions
    ) {
        return MemberProfile.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .profileImageUrl(member.getProfileImageUrl())
                .mannerScore(member.getMannerScore())
                .mannerScoreCount(member.getMannerScoreCount())
                .addressDepth1Name(mainAddress.getAddressDepth1().getName())
                .addressDepth2Name(mainAddress.getAddressDepth2().getName())
                .positions(positions)
                .build();
    }

    public MemberDomain mapToMemberDomain(final Member memberEntity) {
        final Long memberId = memberEntity.getId();
        final MainAddress mainAddress = addressReader.readMainAddressById(
                memberEntity.getAddressDepth1Id(),
                memberEntity.getAddressDepth2Id()
        );

        final List<Position> positions = memberPositionRepository.findAllByMemberId(memberId)
                .stream()
                .map(MemberPosition::getPosition)
                .toList();

        final List<Game> joinedGames = gameMemberRepository.findAllByMemberIdAndStatus(memberId, CONFIRMED)
                .stream()
                .map(gameMember -> gameRepository.getGameById(gameMember.getGameId()))
                .toList();

        return MemberDomain.builder()
                .memberId(memberId)
                .email(memberEntity.getEmail())
                .nickname(memberEntity.getNickname())
                .introduction(memberEntity.getIntroduction())
                .profileImageUrl(memberEntity.getProfileImageUrl())
                .mannerScore(memberEntity.getMannerScore())
                .mannerScoreCount(memberEntity.getMannerScoreCount())
                .addressDepth1Name(mainAddress.getAddressDepth1().getName())
                .addressDepth2Name(mainAddress.getAddressDepth2().getName())
                .positions(positions)
                .joinedCrews(crewReader.readJoinedCrewsByMemberId(memberId))
                .joinedGames(joinedGames)
                .build();
    }
}
