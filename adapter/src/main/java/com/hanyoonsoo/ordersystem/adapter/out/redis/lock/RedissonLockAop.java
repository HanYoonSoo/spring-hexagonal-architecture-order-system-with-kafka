package com.hanyoonsoo.ordersystem.adapter.out.redis.lock;

import com.hanyoonsoo.ordersystem.common.lock.DistributedLock;
import com.hanyoonsoo.ordersystem.common.exception.ErrorCode;
import com.hanyoonsoo.ordersystem.common.exception.base.ConflictException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
@Order(1)
@RequiredArgsConstructor
public class RedissonLockAop {

    private static final String REDISSON_LOCK_PREFIX = "LOCK:";

    private final RedissonClient redissonClient;
    private final AopForTransaction aopForTransaction;

    @Around("@annotation(com.hanyoonsoo.ordersystem.common.lock.DistributedLock)")
    public Object aroundLock(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

        String dynamicKey = CustomSpringELParser.getDynamicValue(
                signature.getParameterNames(),
                joinPoint.getArgs(),
                distributedLock.key()
        );
        String lockKey = REDISSON_LOCK_PREFIX + dynamicKey;

        RLock lock = redissonClient.getLock(lockKey);
        try {
            boolean available = lock.tryLock(
                    distributedLock.waitTime(),
                    distributedLock.leaseTime(),
                    distributedLock.timeUnit()
            );
            if (!available) {
                throw new ConflictException(ErrorCode.LOCK_ACQUISITION_FAILED);
            }
            return aopForTransaction.proceed(joinPoint);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new ConflictException(ErrorCode.LOCK_ACQUISITION_FAILED);
        } finally {
            try {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            } catch (IllegalMonitorStateException exception) {
                log.warn("Lock already released. key={}", lockKey);
            }
        }
    }
}
