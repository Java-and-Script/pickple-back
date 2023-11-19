package kr.pickple.back.alaram.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import kr.pickple.back.alaram.util.AlaramStatusConverter;
import kr.pickple.back.alaram.util.AlaramTypeConverter;
import kr.pickple.back.common.domain.BaseEntity;
import kr.pickple.back.crew.domain.Crew;
import kr.pickple.back.member.domain.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

import static kr.pickple.back.alaram.domain.AlaramStatus.FALSE;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CrewAlaram extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(length = 10)
    @Convert(converter = AlaramStatusConverter.class)
    private AlaramStatus isRead = FALSE;

    @NotNull
    @Column(length = 20)
    @Convert(converter = AlaramTypeConverter.class)
    private AlaramType alaramType;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crew_id")
    private Crew crew;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    private CrewAlaram(
            final AlaramType alaramType,
            final Crew crew,
            final Member member
    ) {
        this.alaramType = alaramType;
        this.createdAt = super.getCreatedAt();
        this.crew = crew;
        this.member = member;
    }
}
