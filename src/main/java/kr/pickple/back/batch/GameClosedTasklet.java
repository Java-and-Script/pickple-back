package kr.pickple.back.batch;

import static kr.pickple.back.game.domain.GameStatus.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import kr.pickple.back.game.domain.Game;
import kr.pickple.back.game.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class GameClosedTasklet implements Tasklet {

    private final GameRepository gameRepository;

    @Override
    public RepeatStatus execute(final StepContribution contribution, final ChunkContext chunkContext) {
        log.info("[Batch Start] - Update Game Status");

        final LocalDateTime nowDateTime = LocalDateTime.now();
        final List<Game> openGames = gameRepository.findGamesByStatusAndPlayDateStartTimeBeforeNow(OPEN,
                nowDateTime);

        openGames.forEach(openGame -> {
            openGame.updateGameStatus(CLOSED);
            log.info("[Batch Processing] - OPEN -> CLOSED updatedGameId: {}", openGame.getId());
        });

        log.info("[Batch End] - OPEN -> CLOSED UpdatedGameCount: {}", openGames.size());

        return RepeatStatus.FINISHED;
    }
}