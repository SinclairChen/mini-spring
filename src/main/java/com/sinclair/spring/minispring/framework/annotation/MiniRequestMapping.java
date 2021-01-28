package com.sinclair.spring.minispring.framework.annotation;

import java.lang.annotation.*;

/**
 * @Description 请求处理
 * @Date 2021/1/28 12:01
 * @Author by Saiyong.Chen
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MiniRequestMapping {

    String value() default "";
}
