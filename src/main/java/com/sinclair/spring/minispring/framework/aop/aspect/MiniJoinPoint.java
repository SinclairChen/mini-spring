package com.sinclair.spring.minispring.framework.aop.aspect;

import java.lang.reflect.Method;

/**
 * @Description TODO
 * @Date 2021/2/1 16:16
 * @Author by Saiyong.Chen
 */
public interface MiniJoinPoint {

    Object getThis();

    Object[] getArguments();

    Method getMethod();

    void setUserAttribute(String key, Object value);

    Object getUserAttribute(String key);

}
