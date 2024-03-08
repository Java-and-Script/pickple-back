package kr.pickple.back.game.repository;

import static kr.pickple.back.game.repository.entity.QGameEntity.*;
import static kr.pickple.back.map.domain.QMapPolygon.*;

import java.util.List;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.pickple.back.address.repository.entity.AddressDepth1Entity;
import kr.pickple.back.address.repository.entity.AddressDepth2Entity;
import kr.pickple.back.game.repository.entity.GameEntity;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GameSearchRepositoryImpl implements GameSearchRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<GameEntity> findGamesWithInDistance(
            final Double latitude,
            final Double longitude,
            final Double distance
    ) {
        final String pointWKT = String.format("POINT(%s %s)", latitude, longitude);

        return jpaQueryFactory
                .selectFrom(gameEntity)
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
        ).asc();
    }

    @Override
    public List<GameEntity> findGamesWithInAddress(
            final AddressDepth1Entity addressDepth1Entity,
            final AddressDepth2Entity addressDepth2Entity
    ) {
        return jpaQueryFactory
                .select(gameEntity)
                .from(gameEntity)
                .join(mapPolygon).on(isWithInAddress())
                .where(isAddress(addressDepth1Entity, addressDepth2Entity))
                .orderBy(getOrderByAddress())
                .fetch();
    }

    private BooleanExpression isWithInAddress() {
        return Expressions.booleanTemplate(
                "ST_Contains({0}, {1})",
                mapPolygon.polygon,
                gameEntity.point
        );
    }

    private BooleanExpression isAddress(
            final AddressDepth1Entity addressDepth1Entity,
            final AddressDepth2Entity addressDepth2Entity
    ) {
        return isAddressDepth1(addressDepth1Entity).and(isAddressDepth2(addressDepth2Entity));
    }

    private BooleanExpression isAddressDepth1(final AddressDepth1Entity addressDepth1Entity) {
        return mapPolygon.addressDepth1.eq(addressDepth1Entity);
    }

    private BooleanExpression isAddressDepth2(final AddressDepth2Entity addressDepth2Entity) {
        return mapPolygon.addressDepth2.eq(addressDepth2Entity);
    }

    private OrderSpecifier<Double> getOrderByAddress() {
        return Expressions.numberTemplate(
                Double.class,
                "ST_Distance_Sphere({0}, ST_GeomFromText('POINT(' || {1} || ' ' || {2} || ')', 4326))",
                gameEntity.point, mapPolygon.latitude, mapPolygon.longitude
        ).asc();
    }
}
