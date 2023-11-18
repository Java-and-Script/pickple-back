package kr.pickple.back.game.repository;

import static kr.pickple.back.game.domain.QGame.*;

import java.util.List;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.pickple.back.game.domain.Game;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GameSearchRepositoryImpl implements GameSearchRepository {

    private final JPAQueryFactory jpaQueryFactory;

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
}
