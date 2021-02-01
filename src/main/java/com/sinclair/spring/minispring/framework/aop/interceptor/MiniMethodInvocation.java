package com.sinclair.spring.minispring.framework.aop.interceptor;

import com.sinclair.spring.minispring.framework.aop.aspect.MiniJoinPoint;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * @Description TODO
 * @Date 2021/2/1 15:25
 * @Author by Saiyong.Chen
 */
public class MiniMethodInvocation implements MiniJoinPoint {

    private final Object proxy;
    private final Object target;
    private final Class<?> targetClass;
    private final Method method;
    private Object[] arguments = new Object[0];

    private Map<String, Object> userAttributes;

    private final List<?> interceptorsAndDynamicMethodMatchers;

    private int currentInterceptorIndex = -1;

    public MiniMethodInvocation(Object proxy, Object target, Method method, Object[] arguments, Class<?> targetClass, List<Object> interceptorsAndDynamicMethodMatchers) {
        this.proxy = proxy;
        this.target = target;
        this.targetClass = targetClass;
        this.method = method;
        this.arguments = arguments;
        this.interceptorsAndDynamicMethodMatchers = interceptorsAndDynamicMethodMatchers;
    }

    /**
     * 通知责任链调用方法
     *
     * @return
     * @throws Throwable
     */
    public Object proceed() throws Throwable {
        if (this.currentInterceptorIndex == this.interceptorsAndDynamicMethodMatchers.size() - 1) {
            return method.invoke(target, arguments);
        }

        Object interceptorOrInterceptionAdvice = this.interceptorsAndDynamicMethodMatchers.get(++this.currentInterceptorIndex);

        if (interceptorOrInterceptionAdvice instanceof MiniMethodInterceptor) {
            MiniMethodInterceptor mi = (MiniMethodInterceptor) interceptorOrInterceptionAdvice;
            return mi.invoke(this);
        } else {
            return proceed();
        }

    }

    @Override
    public Object getThis() {
        return this.target;
    }

    @Override
    public Object[] getArguments() {
        return this.arguments;
    }

    @Override
    public Method getMethod() {
        return this.method;
    }

    @Override
    public void setUserAttribute(String key, Object value) {
        this.userAttributes.put(key, value);
    }

    @Override
    public Object getUserAttribute(String key) {
        return this.userAttributes.get(key);
    }
}
