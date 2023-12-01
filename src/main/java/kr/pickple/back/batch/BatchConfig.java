package kr.pickple.back.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import kr.pickple.back.game.repository.GameRepository;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class BatchConfig extends DefaultBatchConfiguration {

    private final GameRepository gameRepository;

    @Bean
    public Job job(
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
    public Tasklet gameClosedTasklet() {
        return new GameClosedTasklet(gameRepository);
    }

    @Bean
    public Tasklet gameEndedTasklet() {
        return new GameEndedTasklet(gameRepository);
    }

    @Bean
    public Step updateGameStatusToClosedStep(
            final JobRepository jobRepository,
            final Tasklet gameClosedTasklet,
            final PlatformTransactionManager transactionManager
    ) {
        return new StepBuilder("updateGameStatusToClosedStep", jobRepository)
                .tasklet(gameClosedTasklet, transactionManager)
                .build();
    }

    @Bean
    public Step updateGameStatusToEndedStep(
            final JobRepository jobRepository,
            final Tasklet gameEndedTasklet,
            final PlatformTransactionManager transactionManager
    ) {
        return new StepBuilder("updateGameStatusToEndedStep", jobRepository)
                .tasklet(gameEndedTasklet, transactionManager)
                .build();
    }
}
