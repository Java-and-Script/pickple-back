package kr.pickple.back.alarm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsynConfig {

    @Bean
    public TaskExecutor taskExecutor() {
        return CustomThreadPoolTaskExecutor.builder()
                .corePoolSize(30)
                .maxPoolSize(50)
                .queueCapacity(70)
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
