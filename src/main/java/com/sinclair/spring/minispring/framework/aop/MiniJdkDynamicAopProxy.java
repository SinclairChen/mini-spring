package com.sinclair.spring.minispring.framework.aop;

import com.sinclair.spring.minispring.framework.aop.aspect.MiniAdvice;
import com.sinclair.spring.minispring.framework.aop.support.MiniAdviceSupport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * @Description jdk动态代理，负责生成代理类
 * @Date 2021/1/29 7:08
 * @Author by Saiyong.Chen
 */
public class MiniJdkDynamicAopProxy implements InvocationHandler {

    private MiniAdviceSupport adviceSupport;

    public MiniJdkDynamicAopProxy(MiniAdviceSupport adviceSupport) {
        this.adviceSupport = adviceSupport;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Map<String, MiniAdvice> adviceMap = adviceSupport.getAdvice(method, null);

        try {
            //前置通知
            invokeAdvice(adviceMap.get("before"));

            method.invoke(this.adviceSupport.getTarget(), args);

            //后置通知
            invokeAdvice(adviceMap.get("after"));
        } catch (Exception e) {
            invokeAdvice(adviceMap.get("afterThrowing"));
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 调用织入的方法
     *
     * @param advice
     */
    private void invokeAdvice(MiniAdvice advice) {
        try {
            advice.getAdviceMethod().invoke(advice.getAspect());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Object getProxy() {
        return Proxy.newProxyInstance(this.getClass().getClassLoader(), this.adviceSupport.getBeanClass().getInterfaces(), this);
    }
}
