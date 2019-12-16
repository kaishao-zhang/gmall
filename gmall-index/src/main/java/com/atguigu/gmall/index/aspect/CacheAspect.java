package com.atguigu.gmall.index.aspect;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.index.annotation.GmallCache;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import springfox.documentation.spring.web.json.Json;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author 凯少
 * @create 2019-12-14 13:09
 */
@Component
@Aspect
public class CacheAspect {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redissonClient;

    @Around("@annotation(com.atguigu.gmall.index.annotation.GmallCache)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable{
        //获取签名
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        //根据签名获取目标方法
        Method method = signature.getMethod();
        //根据joinPoint获取目标方法的参数
        Object[] args = joinPoint.getArgs();
        //获取方法的注解
        GmallCache gmallCache = method.getAnnotation(GmallCache.class);
        //获取存放redis中的key的前缀
        String prefix = gmallCache.prefix();
        //存放redis中key的后缀
        String suffix =  Arrays.asList(args).toString();
        //获取redis存放过期的时间
        int timeout = gmallCache.timeout();
        //获取注解中的随机数的范围
        int random = gmallCache.random();
        //获取方法的返回值类型
        Class returnType = signature.getReturnType();
        //判断redis中是否已经有对应的key
        String key = prefix +suffix;
        String s = redisTemplate.opsForValue().get(key);
        if (StringUtils.isNotBlank(s)) {
            return JSON.parseObject(s,returnType);
        }
        RLock lock = redissonClient.getLock("lock" + suffix);
        lock.lock();
        //再次判断redis中是否已经有对应的key
        String s1 = redisTemplate.opsForValue().get(prefix +suffix);
        if (StringUtils.isNotBlank(s1)) {
            lock.unlock();
            return JSON.parseObject(s1,returnType);
        }

        Object result = joinPoint.proceed();
        //将查询出来的结果放进Redis缓存中
        redisTemplate.opsForValue().set(key,JSON.toJSONString(result));
        lock.unlock();
        return result;

    }

}
