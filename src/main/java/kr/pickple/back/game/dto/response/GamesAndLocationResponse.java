package kr.pickple.back.game.dto.response;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.locationtech.jts.geom.Coordinate;

import kr.pickple.back.map.domain.MapPolygon;
import kr.pickple.back.map.dto.response.Location;
import kr.pickple.back.map.dto.response.PolygonLocation;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GamesAndLocationResponse {

    private List<GameResponse> games;
    private Location location;
    private List<PolygonLocation> polygon;

    public static GamesAndLocationResponse of(final List<GameResponse> gameResponses, final MapPolygon polygon) {
        final Location location = Location.builder()
                .latitude(polygon.getLatitude())
                .longitude(polygon.getLongitude())
                .build();

        final Coordinate[] coordinates = polygon.getPolygon().getCoordinates();

        final List<PolygonLocation> polygonLocations = Arrays.stream(coordinates)
                .map(data -> PolygonLocation.builder()
                        .lat(BigDecimal.valueOf(data.y))
                        .lng(BigDecimal.valueOf(data.x))
                        .build()
                )
                .toList();

        return GamesAndLocationResponse.builder()
                .games(gameResponses)
                .location(location)
                .polygon(polygonLocations)
                .build();
    }
}
