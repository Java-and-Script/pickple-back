package kr.pickple.back.ranking.controller;

import static org.springframework.http.HttpStatus.*;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.pickple.back.ranking.dto.CrewRankingResponse;
import kr.pickple.back.ranking.service.RankingService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ranking")
public class RankingController {

    private final RankingService rankingService;

    @GetMapping("/crews")
    public ResponseEntity<List<CrewRankingResponse>> findCrewRanking() {
        return ResponseEntity.status(OK)
                .body(rankingService.findCrewRanking());
    }
}
