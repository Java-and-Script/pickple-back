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

@Component
@Slf4j
@RequiredArgsConstructor
public class BatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job job;

    @Scheduled(cron = "0 0/30 * * * *")
    public void runJob() {
        try {
            jobLauncher.run(
                    job,
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
