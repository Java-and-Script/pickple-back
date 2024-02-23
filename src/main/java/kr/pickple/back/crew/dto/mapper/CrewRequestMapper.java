package kr.pickple.back.crew.dto.mapper;

import kr.pickple.back.crew.domain.NewCrew;
import kr.pickple.back.crew.dto.request.CrewCreateRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CrewRequestMapper {

    public static NewCrew mapToNewCrewDomain(final CrewCreateRequest crewCreateRequest) {
        return NewCrew.builder()
                .name(crewCreateRequest.getName())
                .content(crewCreateRequest.getContent())
                .maxMemberCount(crewCreateRequest.getMaxMemberCount())
                .addressDepth1Name(crewCreateRequest.getAddressDepth1())
                .addressDepth2Name(crewCreateRequest.getAddressDepth2())
                .build();
    }
}
