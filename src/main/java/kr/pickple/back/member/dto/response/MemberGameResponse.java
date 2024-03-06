package kr.pickple.back.member.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import kr.pickple.back.address.dto.response.MainAddress;
import kr.pickple.back.game.repository.entity.GameEntity;
import kr.pickple.back.game.repository.entity.GameMemberEntity;
import kr.pickple.back.game.domain.GameStatus;
import kr.pickple.back.position.domain.Position;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberGameResponse {

    private Long id;
    private String content;
    private LocalDate playDate;
    private LocalTime playStartTime;
    private LocalTime playEndTime;
    private Integer playTimeMinutes;
    private String mainAddress;
    private String detailAddress;
    private Double latitude;
    private Double longitude;
    private GameStatus status;
    private Boolean isReviewDone;
    private Integer viewCount;
    private Integer cost;
    private Integer memberCount;
    private Integer maxMemberCount;
    private MemberResponse host;
    private String addressDepth1;
    private String addressDepth2;
    private List<Position> positions;
    private List<MemberResponse> members;

    public static MemberGameResponse of(final GameMemberEntity gameMemberEntity, final GameEntity gameEntity, final List<MemberResponse> memberResponses,
            final List<Position> positions, final MainAddress mainAddress) {
        return MemberGameResponse.builder()
                .id(gameEntity.getId())
                .content(gameEntity.getContent())
                .playDate(gameEntity.getPlayDate())
                .playStartTime(gameEntity.getPlayStartTime())
                .playEndTime(gameEntity.getPlayEndTime())
                .playTimeMinutes(gameEntity.getPlayTimeMinutes())
                .mainAddress(gameEntity.getMainAddress())
                .detailAddress(gameEntity.getDetailAddress())
                .latitude(gameEntity.getPoint().getY())
                .longitude(gameEntity.getPoint().getX())
                .status(gameEntity.getStatus())
                .isReviewDone(gameMemberEntity.isAlreadyReviewDone())
                .viewCount(gameEntity.getViewCount())
                .cost(gameEntity.getCost())
                .memberCount(gameEntity.getMemberCount())
                .maxMemberCount(gameEntity.getMaxMemberCount())
                .host(getHostResponse(memberResponses, gameEntity.getHostId()))
                .addressDepth1(mainAddress.getAddressDepth1().getName())
                .addressDepth2(mainAddress.getAddressDepth2().getName())
                .positions(positions)
                .members(memberResponses)
                .build();
    }

    private static MemberResponse getHostResponse(final List<MemberResponse> memberResponses, final Long hostId) {
        return memberResponses.stream()
                .filter(memberResponse -> memberResponse.getId().equals(hostId))
                .findFirst()
                .orElseThrow();
    }
}
