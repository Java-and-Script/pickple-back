package kr.pickple.back.address.dto.kakao;

import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KakaoAddressResponse {

    private List<Document> documents;

    public Point toPoint() {
        final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

        return geometryFactory.createPoint(new Coordinate(documents.get(0).x, documents.get(0).y));
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    static class Document {

        private String addressName;
        private Double x;
        private Double y;
    }
}
