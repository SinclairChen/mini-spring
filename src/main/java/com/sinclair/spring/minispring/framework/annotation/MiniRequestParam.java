package com.sinclair.spring.minispring.framework.annotation;

import java.lang.annotation.*;

/**
 * @Description 清求参数映射
 * @Date 2021/1/28 12:04
 * @Author by Saiyong.Chen
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MiniRequestParam {

    String value() default "";
}
