package kr.pickple.back.crew.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kr.pickple.back.address.dto.response.MainAddressResponse;
import kr.pickple.back.crew.domain.Crew;
import kr.pickple.back.member.domain.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CrewCreateRequest {

    @NotBlank(message = "크루 이름은 필수입니다.")
    @Size(min = 1, max = 20, message = "크루 이름은 20자 이내로 작성해주세요.")
    private String name;

    @Size(max = 1000, message = "크루 소개글은 1,000자를 넘길 수 없습니다.")
    private String content;

    @NotNull(message = "크루의 최대 인원은 null값이 들어올 수 없습니다.")
    @Min(value = 1, message = "크루의 인원은 최소 1명 이상이어야 합니다.")
    @Max(value = 30, message = "크루의 인원은 최대 30명까지만 가능합니다.")
    private Integer maxMemberCount;

    @NotBlank(message = "해당 크루의 활동 장소(도,시) 정보는 필수입니다.")
    private String addressDepth1;

    @NotBlank(message = "해당 크루의 활동 장소(구) 정보는 필수입니다.")
    private String addressDepth2;

    public Crew toEntity(
            final Member leader,
            final MainAddressResponse mainAddressResponse,
            final String profile,
            final String background) {

        return Crew.builder()
                .name(name)
                .content(content)
                .maxMemberCount(maxMemberCount)
                .leader(leader)
                .profileImageUrl(profile)
                .backgroundImageUrl(background)
                .addressDepth1(mainAddressResponse.getAddressDepth1())
                .addressDepth2(mainAddressResponse.getAddressDepth2())
                .build();
    }
}
