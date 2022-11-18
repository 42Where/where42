package openproject.where42.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@EnableAsync
@Configuration
public class AsyncConfig {
    @Bean(name = "apiThreadPoolTaskExecutor")
    public Executor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(2); // 기본 스레드 수
        taskExecutor.setMaxPoolSize(2); // 최대 스레드 수
        taskExecutor.setQueueCapacity(300); // Queue 사이즈
        taskExecutor.initialize();
        taskExecutor.setThreadNamePrefix("ApiExecutor-");
        return taskExecutor;
    }
}
