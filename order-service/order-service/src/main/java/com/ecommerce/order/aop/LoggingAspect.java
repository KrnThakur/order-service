// src/main/java/com/ecommerce/order/aop/LoggingAspect.java
package com.ecommerce.order.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Before("execution(* com.ecommerce.order.service.*.*(..))")
    public void logBeforeServiceMethod(JoinPoint jp) {
        log.info("→ Service Method: {} called with args: {}",
                jp.getSignature().getName(), jp.getArgs());
    }

    @AfterReturning(pointcut = "execution(* com.ecommerce.order.service.*.*(..))", returning = "result")
    public void logAfterReturning(JoinPoint jp, Object result) {
        log.info("← Service Method: {} completed. Returned: {}", jp.getSignature().getName(), result);
    }

    @Around("execution(* com.ecommerce.order.controller.*.*(..))")
    public Object logControllerExecutionTime(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = pjp.proceed();
        long time = System.currentTimeMillis() - start;
        log.info("Controller {} executed in {} ms", pjp.getSignature().getName(), time);
        return result;
    }
}