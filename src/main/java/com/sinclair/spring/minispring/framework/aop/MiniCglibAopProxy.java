package com.sinclair.spring.minispring.framework.aop;

import com.sinclair.spring.minispring.framework.aop.support.MiniAdviceSupport;

/**
 * @Description cglib动态代理，生成代理类
 * @Date 2021/2/1 9:54
 * @Author by Saiyong.Chen
 */
public class MiniCglibAopProxy implements MiniAopProxy {

    private MiniAdviceSupport config;

    public MiniCglibAopProxy(MiniAdviceSupport config) {
        this.config =config;
    }

    @Override
    public Object getProxy() {
        return null;
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return null;
    }
}
