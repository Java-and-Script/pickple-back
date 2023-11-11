package kr.pickple.back.game.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import kr.pickple.back.address.dto.response.MainAddressResponse;
import kr.pickple.back.game.domain.Game;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.position.domain.Position;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameCreateRequest {

    @NotBlank(message = "모집 글 내용은 null이거나, 빈 문자열이거나, 공백 문자만으로 이루어질 수 없음")
    @Size(max = 1000, message = "모집 글 내용은 1000자 이하")
    private String content;

    @NotNull(message = "경기 날짜는 null일 수 없음")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate playDate;

    @NotNull(message = "경기 시간은 null일 수 없음")
    @JsonFormat(pattern = "HH:mm", timezone = "Asia/Seoul")
    private LocalTime playStartTime;

    @NotNull(message = "경기 진행 시간(분)은 null일 수 없음")
    @Min(value = 30, message = "경기 진행 시간(분)은 1000자 이하")
    @Max(value = 360, message = "경기 진행 시간(분)은 1000자 이하")
    private Integer playTimeMinutes;

    @NotBlank(message = "메인 주소(도, 시, 구, 동, 번지)는 null이거나, 빈 문자열이거나, 공백 문자만으로 이루어질 수 없음")
    @Size(max = 50, message = "메인 주소(도, 시, 구, 동, 번지)는 50자 이하")
    private String mainAddress;

    @NotNull(message = "상세 주소(층, 호수)는 null일 수 없음")
    @Size(max = 50, message = "상세 주소(층, 호수)는 50자 이하")
    private String detailAddress;

    @NotNull(message = "참여 비용은 null일 수 없음")
    @PositiveOrZero(message = "참여 비용은 0원 이상, 100,000원 이하")
    private Integer cost;

    @NotNull(message = "모집 인원은 null일 수 없음")
    @Positive(message = "모집 인원은 1 이상, 15이하")
    private Integer maxMemberCount;

    @NotNull(message = "포지션 목록은 null일 수 없음")
    private List<Position> positions;

    public Game toEntity(final Member host, final MainAddressResponse mainAddressResponse) {
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
                .positions(positions)
                .build();
    }
}
