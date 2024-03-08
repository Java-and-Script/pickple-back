package kr.pickple.back.address.domain;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AllAddress {

    private String addressDepth1Name;
    private List<String> addressDepth2Names;
}
