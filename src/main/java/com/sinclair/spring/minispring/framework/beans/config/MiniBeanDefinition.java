package com.sinclair.spring.minispring.framework.beans.config;

/**
 * @Description beanDefinition 封装类
 * @Date 2021/1/28 13:07
 * @Author by Saiyong.Chen
 */
public class MiniBeanDefinition {

    /** 在工厂类中的名字*/
    private String factoryBeanName;

    /** bean的类名 */
    private String beanClassName;

    public String getFactoryBeanName() {
        return factoryBeanName;
    }

    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }

    public String getBeanClassName() {
        return beanClassName;
    }

    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }
}
