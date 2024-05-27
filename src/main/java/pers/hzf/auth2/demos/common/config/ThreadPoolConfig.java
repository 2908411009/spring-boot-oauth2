package pers.hzf.auth2.demos.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import pers.hzf.auth2.demos.common.thread.TaskExecutorMdcWrapper;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author houzhifang
 * @date 2024/4/25 15:29
 */
@EnableAsync
@Configuration
public class ThreadPoolConfig {

    @Primary
    @Bean("hzf.taskExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new TaskExecutorMdcWrapper();
        taskExecutor.setThreadNamePrefix("hzf.taskExecutor-");
        taskExecutor.setCorePoolSize(2);
        taskExecutor.setMaxPoolSize(20);
        taskExecutor.setQueueCapacity(0);
        taskExecutor.setKeepAliveSeconds(60);
        taskExecutor.setThreadFactory(new ThreadFactory() {
            AtomicInteger atomicInteger = new AtomicInteger(1);
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName(taskExecutor.getThreadNamePrefix() + atomicInteger.getAndIncrement());
                thread.setPriority(1);
                return thread;
            }
        });
        taskExecutor.setAwaitTerminationSeconds(120);
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        taskExecutor.initialize();
        return taskExecutor;
    }

}
