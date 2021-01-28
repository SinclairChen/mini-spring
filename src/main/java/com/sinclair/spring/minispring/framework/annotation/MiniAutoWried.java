package com.sinclair.spring.minispring.framework.annotation;

import java.lang.annotation.*;

/**
 * @Description 自动注入
 * @Date 2021/1/28 11:57
 * @Author by Saiyong.Chen
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MiniAutoWried {

    String value() default "";
}
