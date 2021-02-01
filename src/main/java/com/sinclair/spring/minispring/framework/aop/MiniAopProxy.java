package com.sinclair.spring.minispring.framework.aop;

/**
 * @Description 动态代理定义接口
 * @Date 2021/2/1 9:51
 * @Author by Saiyong.Chen
 */
public interface MiniAopProxy {

     Object getProxy();


     Object getProxy(ClassLoader classLoader);
}
