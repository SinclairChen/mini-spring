package com.sinclair.spring.minispring.framework.aop.aspect;

import com.sinclair.spring.minispring.framework.aop.interceptor.MiniMethodInterceptor;
import com.sinclair.spring.minispring.framework.aop.interceptor.MiniMethodInvocation;

import java.lang.reflect.Method;

/**
 * @Description 后置通知拦截器
 * @Date 2021/2/1 16:05
 * @Author by Saiyong.Chen
 */
public class MiniAfterReturningAdviceInterceptor extends MiniAbstractAspectJAdvice implements MiniMethodInterceptor {

    private MiniJoinPoint joinPoint;

    public MiniAfterReturningAdviceInterceptor(Object newInstance, Method adviceMethod) {
        super(newInstance, adviceMethod);
    }


    @Override
    public Object invoke(MiniMethodInvocation invocation) throws Throwable {
        this.joinPoint = invocation;
        Object returnValue = invocation.proceed();
        this.afterReturning(returnValue, invocation.getMethod(), invocation.getArguments(), invocation.getThis());
        return returnValue;
    }

    protected void afterReturning(Object returnValue, Method method, Object[] arguments, Object aThis) throws Throwable{
        this.invokeAdviceMethod(this.joinPoint, returnValue, null);
    }
}
