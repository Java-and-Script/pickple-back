package kr.pickple.back.game.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import kr.pickple.back.position.domain.Position;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class GamePositionJdbcRepository {

    private static final String GAME_POSITION_INSERT_SQL = "INSERT INTO game_position (position, game_id) VALUES(?, ?)";

    private final JdbcTemplate jdbcTemplate;

    public void creatGamePositions(final List<Position> positions, final Long gameId) {
        jdbcTemplate.batchUpdate(
                GAME_POSITION_INSERT_SQL,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        Position position = positions.get(i);
                        ps.setString(1, position.getAcronym());
                        ps.setLong(2, gameId);
                    }

                    @Override
                    public int getBatchSize() {
                        return positions.size();
                    }
                }
        );
    }
}



