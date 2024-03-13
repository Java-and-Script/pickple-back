package kr.pickple.back.member.repository;

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
public class MemberPositionJdbcRepository {

    private static final String MEMBER_POSITION_INSERT_SQL = "INSERT INTO member_position (position, member_id) VALUES(?, ?)";

    private final JdbcTemplate jdbcTemplate;

    public void creatMemberPositions(final List<Position> positions, final Long memberId) {
        jdbcTemplate.batchUpdate(
                MEMBER_POSITION_INSERT_SQL,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        Position position = positions.get(i);
                        ps.setString(1, position.getAcronym());
                        ps.setLong(2, memberId);
                    }

                    @Override
                    public int getBatchSize() {
                        return positions.size();
                    }
                }
        );
    }
}
