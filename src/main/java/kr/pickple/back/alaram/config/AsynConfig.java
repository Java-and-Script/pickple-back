package kr.pickple.back.alaram.config;

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
                .corePoolSize(50)
                .maxPoolSize(70)
                .queueCapacity(100)
                .build();
    }

    static class CustomThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {
        private CustomThreadPoolTaskExecutor(int corePoolSize, int maxPoolSize, int queueCapacity) {
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

            public Builder corePoolSize(int corePoolSize) {
                this.corePoolSize = corePoolSize;
                return this;
            }

            public Builder maxPoolSize(int maxPoolSize) {
                this.maxPoolSize = maxPoolSize;
                return this;
            }

            public Builder queueCapacity(int queueCapacity) {
                this.queueCapacity = queueCapacity;
                return this;
            }

            public CustomThreadPoolTaskExecutor build() {
                return new CustomThreadPoolTaskExecutor(corePoolSize, maxPoolSize, queueCapacity);
            }
        }
    }
}
