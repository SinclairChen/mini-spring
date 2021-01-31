package com.sinclair.spring.minispring.framework.beans.support;

import com.sinclair.spring.minispring.framework.beans.core.MiniBeanFactory;
import com.sinclair.spring.minispring.framework.beans.config.MiniBeanDefinition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description beanFactory的默认实现
 * @Date 2021/1/31 15:31
 * @Author by Saiyong.Chen
 */
public class MiniDefaultListableBeanFactory implements MiniBeanFactory {

    /** BeanDefinition缓存容器；用于缓存beanDefinition 便于后续使用 */
    public Map<String, MiniBeanDefinition> beanDefinitionMap = new HashMap<String, MiniBeanDefinition>();

    @Override
    public Object getBean(Class beanClass) {
        return null;
    }

    @Override
    public Object getBean(String beanName) {
        return null;
    }


    /**
     * 将beanDefinition缓存起来
     *
     * @param beanDefinitions
     */
    public void doRegistBeanDefinition(List<MiniBeanDefinition> beanDefinitions) throws Exception {
        for (MiniBeanDefinition beanDefinition : beanDefinitions) {

            if(this.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())) {
                throw new Exception("The " + beanDefinition.getFactoryBeanName() + " is exist!");
            }

            beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
            beanDefinitionMap.put(beanDefinition.getBeanClassName(), beanDefinition);
        }
    }
}
