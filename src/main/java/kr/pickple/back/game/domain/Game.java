package kr.pickple.back.game.domain;

import static kr.pickple.back.game.domain.GameStatus.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import kr.pickple.back.address.domain.AddressDepth1;
import kr.pickple.back.address.domain.AddressDepth2;
import kr.pickple.back.common.domain.BaseEntity;
import kr.pickple.back.common.domain.RegistrationStatus;
import kr.pickple.back.game.util.GameStatusConverter;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.position.domain.Position;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Game extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
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

    private Double latitude;
    private Double longitude;

    @NotNull
    @Convert(converter = GameStatusConverter.class)
    @Column(length = 10)
    private GameStatus status = OPEN;

    //todo 현호: 게시글 상세 조회 기능 구현시 viewCount 올리는 기능 구현
    @NotNull
    private Integer viewCount = 0;

    @NotNull
    private Integer cost = 0;

    @NotNull
    private Integer memberCount = 1;

    @NotNull
    private Integer maxMemberCount = 1;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id")
    private Member host;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_depth1_id")
    private AddressDepth1 addressDepth1;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_depth2_id")
    private AddressDepth2 addressDepth2;

    @Embedded
    private GamePositions gamePositions = new GamePositions();

    @Embedded
    private GameMembers gameMembers = new GameMembers();

    @Builder
    private Game(
            final String content,
            final LocalDate playDate,
            final LocalTime playStartTime,
            final LocalTime playEndTime,
            final Integer playTimeMinutes,
            final String mainAddress,
            final String detailAddress,
            final Integer cost,
            final Integer maxMemberCount,
            final Member host,
            final AddressDepth1 addressDepth1,
            final AddressDepth2 addressDepth2,
            final List<Position> positions
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
        this.host = host;
        this.addressDepth1 = addressDepth1;
        this.addressDepth2 = addressDepth2;
        updateGamePositions(positions);
    }

    private void updateGamePositions(final List<Position> positions) {
        gamePositions.updateGamePositions(this, positions);
    }

    public List<Position> getPositions() {
        return gamePositions.getPositions();
    }

    public List<Member> getMembersByStatus(final RegistrationStatus status) {
        return gameMembers.getMembersByStatus(status);
    }

    public void addGameMember(final Member member) {
        gameMembers.addGameMember(this, member);
    }
}
