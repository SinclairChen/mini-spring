package com.sinclair.spring.minispring.framework.annotation;

/**
 * @Description 业务处理
 * @Date 2021/1/28 12:05
 * @Author by Saiyong.Chen
 */

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MiniService {

    String value() default "";
}
