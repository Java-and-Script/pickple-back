package kr.pickple.back.fixture.dto;

import kr.pickple.back.crew.dto.request.CrewCreateRequest;

public class CrewDtoFixtures {

    public static CrewCreateRequest crewCreateRequestBuild() {
        return CrewCreateRequest.builder()
                .name("백둥크루")
                .content("안녕하세요. 백둥크루 입니다~!")
                .maxMemberCount(15)
                .addressDepth1("서울시")
                .addressDepth2("영등포구")
                .build();
    }
}
