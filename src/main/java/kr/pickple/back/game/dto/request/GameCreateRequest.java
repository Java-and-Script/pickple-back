package kr.pickple.back.game.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kr.pickple.back.address.dto.response.MainAddressResponse;
import kr.pickple.back.game.domain.Game;
import kr.pickple.back.game.dto.validator.PositionsValid;
import kr.pickple.back.member.domain.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameCreateRequest {

    @NotEmpty(message = "모집 글 내용은 null이거나 빈 문자열일 수 없음")
    @Size(max = 1000, message = "모집 글 내용은 1000자 이하")
    private String content;

    @NotNull(message = "경기 날짜는 null일 수 없음")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate playDate;

    @NotNull(message = "경기 시간은 null일 수 없음")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime playStartTime;

    @NotNull(message = "경기 진행 시간(분)은 null일 수 없음")
    @Min(value = 30, message = "경기 진행 시간(분)은 1000자 이하")
    @Max(value = 360, message = "경기 진행 시간(분)은 1000자 이하")
    private Integer playTimeMinutes;

    @NotEmpty(message = "메인 주소(도, 시, 구, 동, 번지)는 null이거나 빈 문자열일 수 없음")
    @Size(max = 50, message = "메인 주소(도, 시, 구, 동, 번지)는 50자 이하")
    private String mainAddress;

    @NotNull(message = "상세 주소(층, 호수)는 null일 수 없음")
    @Size(max = 50, message = "상세 주소(층, 호수)는 50자 이하")
    private String detailAddress;

    @NotNull(message = "참여 비용은 null일 수 없음")
    @Min(value = 0, message = "참여 비용은 0 원 이상")
    @Max(value = 100_000, message = "참여 비용은 100,000 원 이하")
    private Integer cost;

    @NotNull(message = "모집 인원은 null일 수 없음")
    @Min(value = 1, message = "모집 인원은 1 이상")
    @Max(value = 15, message = "모집 인원은 15 이하")
    private Integer maxMemberCount;

    @PositionsValid(message = "포지션 목록은 null일 수 없음\n포지션 목록은 [C, PF, SF, PG, SG, 없음] 중 복수 선택. 같은 포지션을 2회 이상 중복 선택은 불가")
    private List<String> positions;

    @NotNull(message = "호스트 ID는 null일 수 없음")
    private Long hostId;

    public Game toEntity(final MainAddressResponse mainAddressResponse, final Member host) {
        return Game.builder()
                .content(content)
                .playDate(playDate)
                .playStartTime(playStartTime)
                .playEndTime(playStartTime.plusMinutes(playTimeMinutes))
                .playTimeMinutes(playTimeMinutes)
                .mainAddress(mainAddress)
                .detailAddress(detailAddress)
                .cost(cost)
                .maxMemberCount(maxMemberCount)
                .host(host)
                .addressDepth1(mainAddressResponse.getAddressDepth1())
                .addressDepth2(mainAddressResponse.getAddressDepth2())
                .build();
    }
}
