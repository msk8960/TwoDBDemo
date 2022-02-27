package com.example.demo.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Aspect
@Configuration
public class AccountServiceTracker {

    private final Logger log = LoggerFactory.getLogger(AccountServiceTracker.class);

    @Around(value = "execution(* com.example.demo.controller.*.*(..))")
    public Object timeTrackerController(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            log.info("start - entering method {}", joinPoint);

            Object obj = joinPoint.proceed();

            log.info("end - exiting method {}", joinPoint);
            return obj;
        } catch (Exception e) {
            log.error("Error while tracking entry/exit to controller", e);
            throw e;
        }
    }
}
