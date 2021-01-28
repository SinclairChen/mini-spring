package com.sinclair.spring.minispring.framework.annotation;

import java.lang.annotation.*;

/**
 * @Description 页面交互
 * @Date 2021/1/28 11:59
 * @Author by Saiyong.Chen
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MiniController {

    String value() default "";
}
