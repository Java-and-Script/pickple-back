package kr.pickple.back.game.repository;

import static kr.pickple.back.game.domain.QGame.*;
import static kr.pickple.back.map.domain.QMapPolygon.*;

import java.util.List;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.pickple.back.address.domain.AddressDepth1;
import kr.pickple.back.address.domain.AddressDepth2;
import kr.pickple.back.game.domain.Game;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GameSearchRepositoryImpl implements GameSearchRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Game> findGamesWithInDistance(
            final Double latitude,
            final Double longitude,
            final Double distance
    ) {
        final String pointWKT = String.format("POINT(%s %s)", latitude, longitude);

        return jpaQueryFactory
                .selectFrom(game)
                .join(game.host).fetchJoin()
                .join(game.addressDepth1).fetchJoin()
                .join(game.addressDepth2).fetchJoin()
                .leftJoin(game.gameMembers.gameMembers).fetchJoin()
                .where(isWithInDistance(pointWKT, distance))
                .orderBy(getOrderByDistance(pointWKT))
                .fetch();
    }

    private BooleanExpression isWithInDistance(final String pointWKT, final Double distance) {
        return Expressions.booleanTemplate(
                "ST_Contains(ST_Buffer(ST_GeomFromText({0}, 4326), {1}), point)",
                pointWKT,
                distance
        );
    }

    private OrderSpecifier<Double> getOrderByDistance(final String pointWKT) {
        return Expressions.numberTemplate(
                        Double.class,
                        "ST_Distance_Sphere(point, ST_GeomFromText({0}, 4326))",
                        pointWKT
                )
                .asc();
    }

    @Override
    public List<Game> findGamesWithInAddress(
            final AddressDepth1 addressDepth1,
            final AddressDepth2 addressDepth2
    ) {
        return jpaQueryFactory
                .select(game)
                .from(game)
                .join(mapPolygon).on(isWithInAddress())
                .where(isAddress(addressDepth1, addressDepth2))
                .orderBy(getOrderByAddress())
                .fetch();
    }

    private BooleanExpression isWithInAddress() {
        return Expressions.booleanTemplate(
                "ST_Contains({0}, {1})",
                mapPolygon.polygon,
                game.point
        );
    }

    private BooleanExpression isAddress(final AddressDepth1 addressDepth1, final AddressDepth2 addressDepth2) {
        return isAddressDepth1(addressDepth1).and(isAddressDepth2(addressDepth2));
    }

    private BooleanExpression isAddressDepth1(final AddressDepth1 addressDepth1) {
        return mapPolygon.addressDepth1.eq(addressDepth1);
    }

    private BooleanExpression isAddressDepth2(final AddressDepth2 addressDepth2) {
        return mapPolygon.addressDepth2.eq(addressDepth2);
    }

    private OrderSpecifier<Double> getOrderByAddress() {
        return Expressions.numberTemplate(
                        Double.class,
                        "ST_Distance_Sphere({0}, ST_GeomFromText('POINT(' || {1} || ' ' || {2} || ')', 4326))",
                        game.point, mapPolygon.latitude, mapPolygon.longitude
                )
                .asc();
    }
}
