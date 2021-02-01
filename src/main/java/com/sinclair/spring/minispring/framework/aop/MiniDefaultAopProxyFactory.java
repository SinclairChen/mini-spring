package com.sinclair.spring.minispring.framework.aop;

import com.sinclair.spring.minispring.framework.aop.support.MiniAdviceSupport;

/**
 * @Description aop代理工程默认实现,
 * @Date 2021/2/1 17:25
 * @Author by Saiyong.Chen
 */
public class MiniDefaultAopProxyFactory {

    public MiniAopProxy createAopProxy(MiniAdviceSupport config) throws Exception {
        if(config.getBeanClass().getInterfaces().length > 0) {
            return new MiniJdkDynamicAopProxy(config);
        }
        return new MiniCglibAopProxy(config);
    }
}
