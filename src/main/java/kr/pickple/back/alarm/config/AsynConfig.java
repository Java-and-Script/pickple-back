package kr.pickple.back.alarm.config;

import kr.pickple.back.common.config.property.AsyncProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
@RequiredArgsConstructor
public class AsynConfig {

    private final AsyncProperties asyncProperties;

    @Bean
    public TaskExecutor taskExecutor() {
        return CustomThreadPoolTaskExecutor.builder()
                .corePoolSize(asyncProperties.getCorePoolSize())
                .maxPoolSize(asyncProperties.getMaxPoolSize())
                .queueCapacity(asyncProperties.getQueueCapacity())
                .build();
    }

    static class CustomThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {
        private CustomThreadPoolTaskExecutor(final int corePoolSize, final int maxPoolSize, final int queueCapacity) {
            super();
            this.setCorePoolSize(corePoolSize);
            this.setMaxPoolSize(maxPoolSize);
            this.setQueueCapacity(queueCapacity);
            this.initialize();
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private int corePoolSize;
            private int maxPoolSize;
            private int queueCapacity;

            public Builder corePoolSize(final int corePoolSize) {
                this.corePoolSize = corePoolSize;
                return this;
            }

            public Builder maxPoolSize(final int maxPoolSize) {
                this.maxPoolSize = maxPoolSize;
                return this;
            }

            public Builder queueCapacity(final int queueCapacity) {
                this.queueCapacity = queueCapacity;
                return this;
            }

            public CustomThreadPoolTaskExecutor build() {
                return new CustomThreadPoolTaskExecutor(corePoolSize, maxPoolSize, queueCapacity);
            }
        }
    }
}
