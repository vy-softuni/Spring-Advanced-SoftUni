package app.mendnook.hub.shared;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ActionTimingAspect {

    private static final Logger log = LoggerFactory.getLogger(ActionTimingAspect.class);

    @Around("@annotation(trackedAction)")
    Object measureAction(ProceedingJoinPoint joinPoint, TrackedAction trackedAction) throws Throwable {
        long started = System.nanoTime();
        String actor = currentActor();
        try {
            Object result = joinPoint.proceed();
            long elapsedMillis = (System.nanoTime() - started) / 1_000_000;
            log.info("action={} actor={} outcome=success durationMs={}",
                    trackedAction.value(), actor, elapsedMillis);
            return result;
        } catch (RuntimeException exception) {
            log.warn("action={} actor={} outcome=failure reason={}",
                    trackedAction.value(), actor, exception.getMessage());
            throw exception;
        }
    }

    private String currentActor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication == null ? "system" : authentication.getName();
    }
}
