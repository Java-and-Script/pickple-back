package kr.pickple.back.batch;

import java.time.LocalDateTime;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class GameBatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job updateGameStatusJob;

    @Scheduled(cron = "0 0/30 * * * *")
    public void runUpdateGameStatusJob() {
        try {
            jobLauncher.run(
                    updateGameStatusJob,
                    new JobParametersBuilder()
                            .addString("dateTime", LocalDateTime.now().toString())
                            .toJobParameters()
            );
        } catch (
                JobExecutionAlreadyRunningException |
                JobInstanceAlreadyCompleteException |
                JobParametersInvalidException |
                JobRestartException e
        ) {
            log.error("[Scheduler - Batch] run job exception: ", e);
        }
    }
}
