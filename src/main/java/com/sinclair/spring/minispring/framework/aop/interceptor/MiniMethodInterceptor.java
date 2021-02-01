package com.sinclair.spring.minispring.framework.aop.interceptor;

import java.lang.reflect.Method;

/**
 * @Description 通知拦截器接口
 * @Date 2021/2/1 15:22
 * @Author by Saiyong.Chen
 */
public interface MiniMethodInterceptor {

    Object invoke(MiniMethodInvocation invocation) throws  Throwable;

}
