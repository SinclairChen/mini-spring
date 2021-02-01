package com.sinclair.spring.minispring.framework.aop.aspect;

import com.sinclair.spring.minispring.framework.aop.interceptor.MiniMethodInterceptor;
import com.sinclair.spring.minispring.framework.aop.interceptor.MiniMethodInvocation;

import java.lang.reflect.Method;

/**
 * @Description 异常通知拦截器
 * @Date 2021/2/1 16:07
 * @Author by Saiyong.Chen
 */
public class MiniAspectAfterThrowingAdviceInterceptor extends MiniAbstractAspectJAdvice implements MiniMethodInterceptor {

    private MiniJoinPoint joinPoint;

    public MiniAspectAfterThrowingAdviceInterceptor(Object newInstance, Method adviceMethod) {
        super(newInstance, adviceMethod);
    }

    @Override
    public Object invoke(MiniMethodInvocation invocation) throws Throwable {
        try {
            return invocation.proceed();
        } catch (Throwable ex) {
            invokeAdviceMethod(invocation, null, ex);
            throw ex;
        }
    }
}
