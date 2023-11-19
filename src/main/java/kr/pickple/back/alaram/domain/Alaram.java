package kr.pickple.back.alaram.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import kr.pickple.back.alaram.util.AlaramExistsStatusConverter;
import kr.pickple.back.member.domain.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static kr.pickple.back.alaram.domain.AlaramExistsStatus.NOT_EXISTS;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Alaram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(length = 10)
    @Convert(converter = AlaramExistsStatusConverter.class)
    private AlaramExistsStatus alaramExistsStatus = NOT_EXISTS;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_alaram_id")
    private CrewAlaram crewAlaram;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_alaram_id")
    private GameAlaram gameAlaram;

    @Builder
    private Alaram(
            final Member member,
            final CrewAlaram crewAlaram,
            final GameAlaram gameAlaram
    ) {
        this.member = member;
        this.crewAlaram = crewAlaram;
        this.gameAlaram = gameAlaram;
    }
}
