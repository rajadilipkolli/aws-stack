package com.learning.awspring.config;

import java.util.concurrent.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration(proxyBeanMethods = false)
@EnableAsync
class SpringAsyncConfig implements AsyncConfigurer {

    private static final Logger log = LoggerFactory.getLogger(SpringAsyncConfig.class);

    @Override
    public Executor getAsyncExecutor() {
        return new ThreadPoolTaskExecutor();
    }

    // To handle exception when the return type is void
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (throwable, method, params) -> {
            log.error("Exception message - {}", throwable.getMessage());
            log.error("Method name - {}", method.getName());
            for (Object param : params) {
                log.error("Parameter value - {}", param);
            }
        };
    }
}
