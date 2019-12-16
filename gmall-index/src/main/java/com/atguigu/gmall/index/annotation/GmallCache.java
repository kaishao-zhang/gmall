package com.atguigu.gmall.index.annotation;

import java.lang.annotation.*;

/**
 * @author 凯少
 * @create 2019-12-14 11:48
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GmallCache {
    /**
     * 缓存key的前缀
     * @return
     */
    String prefix() default "";

    /**
     * 设置缓存过期的时间，单位分钟
     * @return
     */
    int timeout() default 5;

    /**
     * 为了防止redis的雪崩现象，在缓存时间上增加一个随机值，设置随机值的范围
     * @return
     */
    int random() default 5;

}
