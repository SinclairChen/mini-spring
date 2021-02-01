package com.sinclair.spring.minispring.framework.aop.aspect;

import java.lang.reflect.Method;

/**
 * @Description 通知拦截器的抽象方法，用于定义公用属性，和抽取公用方法
 * @Date 2021/2/1 16:11
 * @Author by Saiyong.Chen
 */
public abstract class MiniAbstractAspectJAdvice implements MiniAdvice{
    /** 保存织入的对象 */
    private Object aspect;

    /** 添加需要增强的方法 */
    private Method adviceMethod;

    /** 保持异常的名字 */
    private String throwName;

    public MiniAbstractAspectJAdvice(Object newInstance, Method adviceMethod) {
        this.aspect = newInstance;
        this.adviceMethod = adviceMethod;
    }

    /**
     * 调用通知方法
     * @param joinPoint
     * @param returnValue
     * @param ex
     * @return
     * @throws Throwable
     */
    public Object invokeAdviceMethod (MiniJoinPoint joinPoint, Object returnValue, Throwable ex) throws Throwable {
        Class<?>[] parameterTypes = this.adviceMethod.getParameterTypes();

        if (parameterTypes == null || parameterTypes.length == 0) {
            return this.adviceMethod.invoke(aspect);
        } else {
            Object[] args = new Object[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; i++) {
                if (parameterTypes[i] == MiniJoinPoint.class) {
                    args[i] = joinPoint;
                }else if(parameterTypes[i] == Throwable.class) {
                    args[i] = ex;
                }else if(parameterTypes[i] == Object.class) {
                    args[i] = returnValue;
                }
            }

            return this.adviceMethod.invoke(aspect, args);
        }
    }



}
