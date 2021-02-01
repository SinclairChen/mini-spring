package com.sinclair.spring.minispring.framework.aop.aspect;

import com.sinclair.spring.minispring.framework.aop.interceptor.MiniMethodInterceptor;
import com.sinclair.spring.minispring.framework.aop.interceptor.MiniMethodInvocation;

import java.lang.reflect.Method;

/**
 * @Description 前置通知拦截器
 * @Date 2021/2/1 16:05
 * @Author by Saiyong.Chen
 */
public class MiniMethodBeforAdviceInterceptor extends MiniAbstractAspectJAdvice implements MiniMethodInterceptor {

    private MiniJoinPoint joinPoint;

    public MiniMethodBeforAdviceInterceptor(Object newInstance, Method adviceMethod) {
        super(newInstance, adviceMethod);
    }


    @Override
    public Object invoke(MiniMethodInvocation invocation) throws Throwable {

        this.joinPoint = invocation;
        this.before(invocation.getMethod(), invocation.getArguments(), null);
        return invocation.proceed();
    }

    public void before(Method method, Object[] args, Object aThis) throws Throwable {
        invokeAdviceMethod(this.joinPoint, null, null);
    }
}
