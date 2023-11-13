package kr.pickple.back.address.dto.kakao;

import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KakaoAddressResponse {

    private List<Document> documents;

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    static class Document {

        private String addressName;
        private Double x;
        private Double y;
    }

    public Coordinate toCoordinate() {
        return Coordinate.builder()
                .x(documents.get(0).x)
                .y(documents.get(0).y)
                .build();
    }
}
