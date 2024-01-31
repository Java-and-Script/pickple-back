package kr.pickple.back.game.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

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

    public static GameResponse of(
            final Game game,
            final MemberResponse host,
            final List<MemberResponse> memberResponses
    ) {
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
                .host(host)
                .addressDepth1(game.getAddressDepth1().getName())
                .addressDepth2(game.getAddressDepth2().getName())
                .positions(game.getPositions())
                .members(memberResponses)
                .build();
    }
}
