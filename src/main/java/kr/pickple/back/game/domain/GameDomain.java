package kr.pickple.back.game.domain;

import static kr.pickple.back.game.domain.GameStatus.*;
import static kr.pickple.back.game.exception.GameExceptionCode.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import kr.pickple.back.chat.domain.ChatRoom;
import kr.pickple.back.crew.exception.CrewException;
import kr.pickple.back.game.exception.GameException;
import kr.pickple.back.member.domain.MemberDomain;
import kr.pickple.back.position.domain.Position;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GameDomain {

    private Long gameId;
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
    private MemberDomain host;
    private String addressDepth1;
    private String addressDepth2;
    private List<Position> positions;
    private ChatRoom chatRoom;

    public void increaseMemberCount() {
        if (status != OPEN) {
            throw new GameException(GAME_STATUS_IS_CLOSED, status);
        }

        if (memberCount.equals(maxMemberCount)) {
            throw new CrewException(GAME_CAPACITY_LIMIT_REACHED, memberCount);
        }

        memberCount += 1;

        if (memberCount.equals(maxMemberCount)) {
            this.status = CLOSED;
        }
    }
    
    public LocalDateTime getPlayEndDatetime() {
        return LocalDateTime.of(playDate, playEndTime);
    }
}
