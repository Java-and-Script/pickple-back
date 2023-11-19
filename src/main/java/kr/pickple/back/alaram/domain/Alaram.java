package kr.pickple.back.alaram.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import kr.pickple.back.alaram.util.AlaramStatusConverter;
import kr.pickple.back.member.domain.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static kr.pickple.back.alaram.domain.AlaramStatus.FALSE;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Alaram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(length = 10)
    @Convert(converter = AlaramStatusConverter.class)
    private AlaramStatus status = FALSE;

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
        this.status = findAlaramUnRead(status);
        this.crewAlaram = crewAlaram;
        this.gameAlaram = gameAlaram;
    }

    private AlaramStatus findAlaramUnRead(AlaramStatus status) {
        //1. 크루 알람 조회 - 상태가 unread가 있는지 체크

        //2. 게임 알람 조회 - 상태가 unread가 있는지 체크

        //3.status 상태 변화

        return this.status;
    }
}
