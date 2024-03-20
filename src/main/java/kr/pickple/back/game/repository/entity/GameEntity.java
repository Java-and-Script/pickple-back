package kr.pickple.back.game.repository.entity;

import static kr.pickple.back.game.domain.GameStatus.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.locationtech.jts.geom.Point;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import kr.pickple.back.common.domain.BaseEntity;
import kr.pickple.back.game.domain.GameStatus;
import kr.pickple.back.game.util.GameStatusConverter;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "game")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000)
    private String content;

    @NotNull
    private LocalDate playDate;

    @NotNull
    private LocalTime playStartTime;

    @NotNull
    private LocalTime playEndTime;

    @NotNull
    private Integer playTimeMinutes;

    @NotNull
    @Column(length = 50)
    private String mainAddress;

    @NotNull
    @Column(length = 50)
    private String detailAddress;

    @NotNull
    private Point point;

    @NotNull
    @Convert(converter = GameStatusConverter.class)
    @Column(length = 10)
    private final GameStatus status = OPEN;

    //todo 현호: 게시글 상세 조회 기능 구현시 viewCount 올리는 기능 구현
    @NotNull
    private Integer viewCount = 0;

    @NotNull
    private Integer cost = 0;

    @NotNull
    private final Integer memberCount = 1;

    @NotNull
    private Integer maxMemberCount = 2;

    @NotNull
    private Long hostId;

    @NotNull
    private Long addressDepth1Id;

    @NotNull
    private Long addressDepth2Id;

    private Long chatRoomId;

    @Builder
    private GameEntity(
            final String content,
            final LocalDate playDate,
            final LocalTime playStartTime,
            final LocalTime playEndTime,
            final Integer playTimeMinutes,
            final String mainAddress,
            final String detailAddress,
            final Integer cost,
            final Integer maxMemberCount,
            final Long hostId,
            final Point point,
            final Long addressDepth1Id,
            final Long addressDepth2Id,
            final Long chatRoomId
    ) {
        this.content = content;
        this.playDate = playDate;
        this.playStartTime = playStartTime;
        this.playEndTime = playEndTime;
        this.playTimeMinutes = playTimeMinutes;
        this.mainAddress = mainAddress;
        this.detailAddress = detailAddress;
        this.cost = cost;
        this.maxMemberCount = maxMemberCount;
        this.hostId = hostId;
        this.point = point;
        this.addressDepth1Id = addressDepth1Id;
        this.addressDepth2Id = addressDepth2Id;
        this.chatRoomId = chatRoomId;
    }

    public LocalDateTime getPlayEndDatetime() {
        return LocalDateTime.of(playDate, playEndTime);
    }

    public void increaseViewCount() {
        viewCount++;
    }

    public Boolean isHost(final Long hostId) {
        return hostId.equals(this.hostId);
    }
}
