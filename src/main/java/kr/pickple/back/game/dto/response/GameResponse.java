package kr.pickple.back.game.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import kr.pickple.back.address.dto.response.MainAddress;
import kr.pickple.back.game.domain.Game;
import kr.pickple.back.game.domain.GameStatus;
import kr.pickple.back.member.dto.response.MemberResponse;
import kr.pickple.back.position.domain.Position;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GameResponse {

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
    private Integer viewCount;
    private Integer cost;
    private Integer memberCount;
    private Integer maxMemberCount;
    private MemberResponse host;
    private String addressDepth1;
    private String addressDepth2;
    private List<Position> positions;
    private List<MemberResponse> members;

    public static GameResponse of(final Game game, final List<MemberResponse> memberResponses,
            final List<Position> positions, final MainAddress mainAddress) {
        return GameResponse.builder()
                .id(game.getId())
                .content(game.getContent())
                .playDate(game.getPlayDate())
                .playStartTime(game.getPlayStartTime())
                .playEndTime(game.getPlayEndTime())
                .playTimeMinutes(game.getPlayTimeMinutes())
                .mainAddress(game.getMainAddress())
                .detailAddress(game.getDetailAddress())
                .latitude(game.getPoint().getY())
                .longitude(game.getPoint().getX())
                .status(game.getStatus())
                .viewCount(game.getViewCount())
                .cost(game.getCost())
                .memberCount(game.getMemberCount())
                .maxMemberCount(game.getMaxMemberCount())
                .host(getHostResponse(memberResponses, game.getHost().getId()))
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
