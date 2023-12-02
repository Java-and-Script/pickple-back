package kr.pickple.back.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class GameBatchConfig extends DefaultBatchConfiguration {

    private final GameEndedTasklet gameEndedTasklet;
    private final GameClosedTasklet gameClosedTasklet;

    @Bean
    public Job updateGameStatusJob(
            final JobRepository jobRepository,
            final Step updateGameStatusToClosedStep,
            final Step updateGameStatusToEndedStep
    ) {
        return new JobBuilder("updateGameStatus", jobRepository)
                .start(updateGameStatusToClosedStep)
                .next(updateGameStatusToEndedStep)
                .build();
    }

    @Bean
    public Step updateGameStatusToClosedStep(
            final JobRepository jobRepository,
            final PlatformTransactionManager platformTransactionManager
    ) {
        return new StepBuilder("updateGameStatusToClosedStep", jobRepository)
                .tasklet(gameClosedTasklet, platformTransactionManager)
                .build();
    }

    @Bean
    public Step updateGameStatusToEndedStep(
            final JobRepository jobRepository,
            final PlatformTransactionManager platformTransactionManager
    ) {
        return new StepBuilder("updateGameStatusToEndedStep", jobRepository)
                .tasklet(gameEndedTasklet, platformTransactionManager)
                .build();
    }
}
