package pers.hzf.auth2.demos.common.thread;

import cn.hutool.core.util.IdUtil;
import com.alibaba.ttl.TtlCallable;
import com.alibaba.ttl.TtlRunnable;
import org.slf4j.MDC;
import pers.hzf.auth2.demos.common.constants.Constants;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * tracerId跟踪：包装Runnable/Callable
 * MDC：与当前线程绑定的哈希表，作用域：当前线程范围内
 *
 * @author houzhifang
 * @date 2024/4/24 17:30
 */
public class ThreadMdcWrapper {

    public static void putIfAbsentByThreadPool() {
        if (MDC.get(Constants.TRACE_ID) == null) {
            MDC.put(Constants.TRACE_ID, getTraceId());
        }
    }
    
    /**
     * 追踪唯一标识
     *
     * @return
     */
    public static String getTraceId() {
        return IdUtil.fastSimpleUUID();
    }

    public static void clear() {
        MDC.clear();
    }
    
    static <T> Callable<T> wrap(final Callable<T> callable){
        return wrap(callable,MDC.getCopyOfContextMap());
    }

    static Runnable wrap(final Runnable runnable){
        return wrap(runnable,MDC.getCopyOfContextMap());
    }
    private static <T> Callable<T> wrap(final Callable<T> callable, final Map<String, String> context) {
        return () -> {
            putContextIfAbsentSetNull(context);
            putIfAbsentByThreadPool();
            try {
                return TtlCallable.get(callable).call();
            } finally {
                clear();
            }
        };
    }

    private static Runnable wrap(final Runnable runnable, final Map<String, String> context) {
        return () -> {
            putContextIfAbsentSetNull(context);
            putIfAbsentByThreadPool();
            try {
                TtlRunnable.get(runnable).run();
            } finally {
                clear();
            }
        };
    }

    private static void putContextIfAbsentSetNull(final Map<String, String> context) {
        if (Objects.nonNull(context)) {
            MDC.setContextMap(context);
        } else {
            MDC.clear();
        }
    }

}
