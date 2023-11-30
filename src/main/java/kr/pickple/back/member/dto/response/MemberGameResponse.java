package kr.pickple.back.member.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import kr.pickple.back.game.domain.Game;
import kr.pickple.back.game.domain.GameMember;
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

    public static MemberGameResponse of(final GameMember gameMember, final List<MemberResponse> memberResponses) {
        final Game game = gameMember.getGame();

        return MemberGameResponse.builder()
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
                .isReviewDone(gameMember.isAlreadyReviewDone())
                .viewCount(game.getViewCount())
                .cost(game.getCost())
                .memberCount(game.getMemberCount())
                .maxMemberCount(game.getMaxMemberCount())
                .host(MemberResponse.from(game.getHost()))
                .addressDepth1(game.getAddressDepth1().getName())
                .addressDepth2(game.getAddressDepth2().getName())
                .positions(game.getPositions())
                .members(memberResponses)
                .build();
    }
}
