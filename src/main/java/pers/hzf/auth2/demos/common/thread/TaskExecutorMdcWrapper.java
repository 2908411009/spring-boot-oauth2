package pers.hzf.auth2.demos.common.thread;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * traceId跟踪: 将父线程的MDC内容复制给子线程
 *
 * @author houzhifang
 * @date 2024/4/24 18:02
 */
@Slf4j
public class TaskExecutorMdcWrapper extends ThreadPoolTaskExecutor {

    private void showThreadPoolInfo(String prefix) {
        ThreadPoolExecutor threadPoolExecutor = this.getThreadPoolExecutor();
        if (threadPoolExecutor == null) {
            return;
        }
        log.debug("{}, {},taskCount [{}], completedCount [{}], activeCount [{}], queueSize [{}]",
                this.getThreadNamePrefix(),
                prefix,
                threadPoolExecutor.getTaskCount(),
                threadPoolExecutor.getCompletedTaskCount(),
                threadPoolExecutor.getActiveCount(),
                threadPoolExecutor.getQueue().size());
    }

    @Override
    public void execute(Runnable task) {
        this.showThreadPoolInfo("1. do execute");
        super.execute(ThreadMdcWrapper.wrap(task));
    }

    @Override
    public Future<?> submit(Runnable task) {
        this.showThreadPoolInfo("1. do submit");
        return super.submit(ThreadMdcWrapper.wrap(task));
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        this.showThreadPoolInfo("2. do submit");
        return super.submit(ThreadMdcWrapper.wrap(task));
    }

    
    
}
