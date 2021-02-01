package com.sinclair.spring.minispring.framework.aop;

import com.sinclair.spring.minispring.framework.aop.aspect.MiniAdvice;
import com.sinclair.spring.minispring.framework.aop.interceptor.MiniMethodInvocation;
import com.sinclair.spring.minispring.framework.aop.support.MiniAdviceSupport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;

/**
 * @Description jdk动态代理，负责生成代理类
 * @Date 2021/1/29 7:08
 * @Author by Saiyong.Chen
 */
public class MiniJdkDynamicAopProxy implements MiniAopProxy, InvocationHandler {

    private MiniAdviceSupport advised;

    public MiniJdkDynamicAopProxy(MiniAdviceSupport adviceSupport) {
        this.advised = adviceSupport;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

//        Map<String, MiniAdvice> adviceMap = advised.getAdvice(method, null);

//        try {
//            //前置通知
//            invokeAdvice(adviceMap.get("before"));
//
//            method.invoke(this.advised.getTarget(), args);
//
//            //后置通知
//            invokeAdvice(adviceMap.get("after"));
//        } catch (Exception e) {
//            invokeAdvice(adviceMap.get("afterThrowing"));
//            e.printStackTrace();
//        }

        List<Object> chain = advised.getInterceptorsAndDynamicInterceptionAdvice(method, this.advised.getBeanClass());

        MiniMethodInvocation invocation = new MiniMethodInvocation(proxy, this.advised.getTarget() , method, args, this.advised.getBeanClass(),chain);
        return invocation.proceed();
    }

    /**
     * 调用织入的方法
     *
     * @param
     */
//    private void invokeAdvice(MiniAdvice advice) {
//        try {
//            advice.getAdviceMethod().invoke(advice.getAspect());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


    @Override
    public Object getProxy() {
        return getProxy(this.getClass().getClassLoader());
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return Proxy.newProxyInstance(classLoader, this.advised.getBeanClass().getInterfaces(), this);
    }
}
