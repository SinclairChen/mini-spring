package com.sinclair.spring.minispring.framework.beans.core;

/**
 * @Description ioc容器的核心接口 bean工厂顶级接口
 * @Date 2021/1/31 15:31
 * @Author by Saiyong.Chen
 */
public interface MiniBeanFactory {
    public Object getBean(Class beanClass) throws Exception;

    /**
     * 通过beanName 从ioc容器中获取一个bean的实例
     * @param beanName
     * @return
     * @throws Exception
     */
    public Object getBean(String beanName) throws Exception;
}
