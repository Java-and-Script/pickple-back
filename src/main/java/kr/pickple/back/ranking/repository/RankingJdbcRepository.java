package kr.pickple.back.ranking.repository;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import kr.pickple.back.ranking.dto.CrewRankingResponse;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RankingJdbcRepository {

    public static final String CREW_RANKING_SQL = """
            SELECT 
                crew_ranking.crew_id AS id, 
                crew_ranking.name, 
                crew_ranking.member_count, 
                crew_ranking.max_member_count, 
                crew_ranking.profile_image_url, 
                crew_ranking.address_depth1, 
                crew_ranking.address_depth2, 
                crew_ranking.activity_score, 
                crew_ranking.manner_score, 
                crew_ranking.activity_score + crew_ranking.manner_score AS total_score, 
                RANK() OVER (
                    ORDER BY crew_ranking.manner_score + crew_ranking.activity_score DESC
                ) AS ranking 
            FROM (
                SELECT 
                    crew_with_manner_score.*, 
                    address_depth1.name AS address_depth1, 
                    address_depth2.name AS address_depth2, 
                    COUNT(
                        DISTINCT CASE WHEN game_member.created_at > DATE_SUB(NOW(), INTERVAL 1 MONTH) 
                        THEN game_member.id ELSE NULL END
                    ) AS activity_score 
                FROM (
                    SELECT 
                        crew.id AS crew_id, 
                        crew.name, 
                        crew.member_count, 
                        crew.max_member_count, 
                        crew.profile_image_url, 
                        crew.address_depth1_id, 
                        crew.address_depth2_id, 
                        SUM(member.manner_score) AS manner_score 
                    FROM 
                        crew 
                    LEFT JOIN 
                        crew_member ON crew.id = crew_member.crew_id 
                    LEFT JOIN 
                        member ON crew_member.member_id = member.id 
                    GROUP BY 
                        crew.id
                ) AS crew_with_manner_score 
                LEFT JOIN 
                    crew_member ON crew_with_manner_score.crew_id = crew_member.crew_id 
                LEFT JOIN 
                    member ON crew_member.member_id = member.id 
                LEFT JOIN 
                    game_member ON member.id = game_member.member_id 
                LEFT JOIN 
                    address_depth1 ON crew_with_manner_score.address_depth1_id = address_depth1.id 
                LEFT JOIN 
                    address_depth2 ON crew_with_manner_score.address_depth2_id = address_depth2.id 
                GROUP BY 
                    crew_with_manner_score.crew_id
            ) AS crew_ranking 
            LIMIT 50;
            """;

    private final JdbcTemplate jdbcTemplate;

    public List<CrewRankingResponse> getCrewRankings() {
        return jdbcTemplate.query(CREW_RANKING_SQL, crewRankingRowMapper());
    }

    private static RowMapper<CrewRankingResponse> crewRankingRowMapper() {
        return (rs, rowNum) -> CrewRankingResponse.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .memberCount(rs.getInt("member_count"))
                .maxMemberCount(rs.getInt("max_member_count"))
                .profileImageUrl(rs.getString("profile_image_url"))
                .addressDepth1(rs.getString("address_depth1"))
                .addressDepth2(rs.getString("address_depth2"))
                .mannerScore(rs.getInt("manner_score"))
                .totalScore(rs.getInt("total_score"))
                .rank(rs.getInt("ranking"))
                .build();
    }
}
