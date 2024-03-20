package kr.pickple.back.game.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import kr.pickple.back.common.domain.BaseEntity;
import kr.pickple.back.position.domain.Position;
import kr.pickple.back.position.util.PositionConverter;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "game_position")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GamePositionEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @NotNull
    @Convert(converter = PositionConverter.class)
    @Column(length = 2)
    private Position position;

    @NotNull
    private Long gameId;

    @Builder
    private GamePositionEntity(final Position position, final Long gameId) {
        this.position = position;
        this.gameId = gameId;
    }
}
