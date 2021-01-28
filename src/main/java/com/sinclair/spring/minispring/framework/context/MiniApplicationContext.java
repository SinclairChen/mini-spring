package com.sinclair.spring.minispring.framework.context;

import com.sinclair.spring.minispring.framework.annotation.MiniAutoWried;
import com.sinclair.spring.minispring.framework.annotation.MiniController;
import com.sinclair.spring.minispring.framework.beans.MiniBeanWrapper;
import com.sinclair.spring.minispring.framework.beans.config.MiniBeanDefinition;
import com.sinclair.spring.minispring.framework.beans.support.MiniBeanDefinitionReader;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 初始化IOC容器，完成Bean的创建和DI
 * @Date 2021/1/28 12:55
 * @Author by Saiyong.Chen
 */
public class MiniApplicationContext {


    private MiniBeanDefinitionReader beanDefinitionReader;

    /** BeanDefinition缓存容器；用于缓存beanDefinition 便于后续使用 */
    private Map<String, MiniBeanDefinition> beanDefinitionMap = new HashMap<String, MiniBeanDefinition>();

    /** ioc 容器 */
    private Map<String, MiniBeanWrapper> factoryBeanInstanceCache = new HashMap<String, MiniBeanWrapper>();

    /** 保存原始的bean实例对象 */
    private Map<String, Object> factoryBeanObjectCache = new HashMap<String, Object>();

    public MiniApplicationContext(String... configLocations) {

        try {
            //1. 读取配置文件
            this.beanDefinitionReader = new MiniBeanDefinitionReader(configLocations);

            //2，解析配置文件，封装成BeanDefinition
            List<MiniBeanDefinition> beanDefinitions = beanDefinitionReader.loadBeanDefinitions();

            //3. 把BeanDefinition缓存起来
            doRegistBeanDefinition(beanDefinitions);

            //4. 依赖注入
            doAutowried();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doAutowried() {

        try {
            for (Map.Entry<String, MiniBeanDefinition> beanDefinitionEntry : this.beanDefinitionMap.entrySet()) {
                String beanName = beanDefinitionEntry.getKey();
                //调用getBean的时候触发实例化
                getBean(beanName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将beanDefinition缓存起来
     *
     * @param beanDefinitions
     */
    private void doRegistBeanDefinition(List<MiniBeanDefinition> beanDefinitions) throws Exception {
        for (MiniBeanDefinition beanDefinition : beanDefinitions) {

            if(this.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())) {
                throw new Exception("The " + beanDefinition.getFactoryBeanName() + " is exist!");
            }

            beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
            beanDefinitionMap.put(beanDefinition.getBeanClassName(), beanDefinition);
        }
    }

    /**
     * 实例化bean，进行DI
     * @param beanName
     * @return
     */
    public Object getBean(String beanName) throws Exception {

        //1.获取MiniDefinition
        MiniBeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);

        //2.反射实例化
        Object instance = instantiateBean(beanName, beanDefinition);

        if (instance == null) {
            throw new Exception( beanName + " is not instance");
        }

        //3. 将实例化后的bean封装成BeanWrapper
        MiniBeanWrapper beanWrapper = new MiniBeanWrapper(instance);

        //4. 将beanwrapper保存到ioc容器中
        factoryBeanInstanceCache.put(beanName, beanWrapper);
        
        //5. 依赖注入
        populateBean(beanName, beanDefinition, beanWrapper);

        return beanWrapper.getWrapperInstance();
    }

    /**
     * 进行依赖注入
     *
     * @param beanName
     * @param beanDefinition
     * @param beanWrapper
     */
    private void populateBean(String beanName, MiniBeanDefinition beanDefinition, MiniBeanWrapper beanWrapper) {

        //涉及到循环依赖
        //我们可以将第一没有注入的，保存起来，然后再次循环一次进行注入


        Object beanInstance = beanWrapper.getWrapperInstance();
        Class<?> beanClass = beanWrapper.getWrapperClass();

        //只有是Controller 和 Service 的类才需要进行依赖注入
        if (!(beanClass.isAnnotationPresent(MiniController.class) || beanClass.isAnnotationPresent(MiniController.class))) {
            return;
        }

        for (Field field : beanClass.getDeclaredFields()) {
            //如果没有标注AutoWried的字段直接跳过
            if (!field.isAnnotationPresent(MiniAutoWried.class)) {
                continue;
            }
            MiniAutoWried autoWried = field.getAnnotation(MiniAutoWried.class);

            String autoWriedBeanName = autoWried.value().trim();
            if ("".equals(autoWriedBeanName)) {
                autoWriedBeanName = field.getType().getName();
            }
            //暴力访问
            field.setAccessible(true);

            try {
                //如果容器中没有直接跳过
                if (!factoryBeanInstanceCache.containsKey(autoWriedBeanName)) {
                    continue;
                }

                field.set(beanInstance, factoryBeanInstanceCache.get(autoWriedBeanName).getWrapperInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                continue;
            }
        }
    }

    /**
     * 实例化Bean对象
     * @param beanName
     * @param beanDefinition
     * @return
     */
    private Object instantiateBean(String beanName, MiniBeanDefinition beanDefinition) {
        String className = beanDefinition.getBeanClassName();

        Object instance = null;
        try {
            //判断下容器中有没有，如果有直接拿，没有再创建
            if (this.factoryBeanInstanceCache.containsKey(beanName)) {
                instance = this.factoryBeanInstanceCache.get(beanName);
            } else {

                Class<?> beanClass = Class.forName(className);
                instance = beanClass.newInstance();

                //缓存没有装饰过的，原始的bean对象
                this.factoryBeanObjectCache.put(beanName, instance);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }

    public Object getBean(Class beanClass) throws Exception {
        return getBean(beanClass.getName());
    }
}
