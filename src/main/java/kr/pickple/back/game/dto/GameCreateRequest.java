package kr.pickple.back.game.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameCreateRequest {
    
    @NotEmpty
    @Size(max = 1000)
    private String content;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate playDate;

    @NotNull
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime playStartTime;

    @NotNull
    @Min(30)
    @Max(360)
    private Integer playTimeMinutes;

    @NotEmpty
    @Size(max = 50)
    private String mainAddress;

    @NotNull
    @Size(max = 50)
    private String detailAddress;

    @NotNull
    @Min(0)
    @Max(100_000)
    private Integer cost;

    @NotNull
    @Min(1)
    @Max(15)
    private Integer maxMemberCount;

    @NotNull
    private Long hostId;
}
