package com.hs.eventio.common;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
class ControllerLoggingAspect {
    private static final Logger LOG = LoggerFactory.getLogger(ControllerLoggingAspect.class);

    @Pointcut(value = "@annotation(org.springframework.web.bind.annotation.GetMapping)")
    void methodsWithGetMappingAnnotation(){}
    @Pointcut(value = "@annotation(org.springframework.web.bind.annotation.PostMapping)")
    void methodsWithPostMappingAnnotation(){}
    @Pointcut(value = "@annotation(org.springframework.web.bind.annotation.PutMapping)")
    void methodsWithPutMappingAnnotation(){}
    @Pointcut(value = "@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    void methodsWithDeleteMappingAnnotation(){}

    @Pointcut(value = "methodsWithGetMappingAnnotation() || methodsWithPostMappingAnnotation()" +
            "|| methodsWithPutMappingAnnotation() || methodsWithDeleteMappingAnnotation()")
    void methodsWithRESTAnnotation(){}

    @Around(value = "methodsWithRESTAnnotation()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        var startTime = System.currentTimeMillis();
        var proceed = joinPoint.proceed();
        var endTime = System.currentTimeMillis();
        LOG.info("{} executes in: {} ms",joinPoint.getSignature().getName(), (endTime-startTime));
        return proceed;
    }
}
