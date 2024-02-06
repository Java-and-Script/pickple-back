package kr.pickple.back.address.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MainAddressId {

    private Long addressDepth1Id;
    private Long addressDepth2Id;
}
