package com.sinclair.spring.minispring.framework.context;

import com.sinclair.spring.minispring.framework.annotation.MiniAutoWried;
import com.sinclair.spring.minispring.framework.annotation.MiniController;
import com.sinclair.spring.minispring.framework.aop.MiniJdkDynamicAopProxy;
import com.sinclair.spring.minispring.framework.aop.config.MiniAopConfig;
import com.sinclair.spring.minispring.framework.aop.support.MiniAdviceSupport;
import com.sinclair.spring.minispring.framework.beans.core.MiniBeanFactory;
import com.sinclair.spring.minispring.framework.beans.MiniBeanWrapper;
import com.sinclair.spring.minispring.framework.beans.config.MiniBeanDefinition;
import com.sinclair.spring.minispring.framework.beans.support.MiniBeanDefinitionReader;
import com.sinclair.spring.minispring.framework.beans.support.MiniDefaultListableBeanFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @Description 初始化IOC容器，完成Bean的创建和DI
 * @Date 2021/1/28 12:55
 * @Author by Saiyong.Chen
 */
public class MiniApplicationContext implements MiniBeanFactory {

    private MiniBeanDefinitionReader beanDefinitionReader;

    private MiniDefaultListableBeanFactory registry = new MiniDefaultListableBeanFactory();

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
            registry.doRegistBeanDefinition(beanDefinitions);

            //4. 依赖注入
            doAutowried();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doAutowried() {

        try {
            for (Map.Entry<String, MiniBeanDefinition> beanDefinitionEntry : registry.beanDefinitionMap.entrySet()) {
                String beanName = beanDefinitionEntry.getKey();
                //调用getBean的时候触发实例化
                getBean(beanName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    /**
//     * 将beanDefinition缓存起来
//     *
//     * @param beanDefinitions
//     */
//    private void doRegistBeanDefinition(List<MiniBeanDefinition> beanDefinitions) throws Exception {
//        for (MiniBeanDefinition beanDefinition : beanDefinitions) {
//
//            if(this.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())) {
//                throw new Exception("The " + beanDefinition.getFactoryBeanName() + " is exist!");
//            }
//
//            beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
//            beanDefinitionMap.put(beanDefinition.getBeanClassName(), beanDefinition);
//        }
//    }

    /**
     * 实例化bean，进行DI
     * @param beanName
     * @return
     */
    @Override
    public Object getBean(String beanName) throws Exception {

        //1.获取MiniDefinition
        MiniBeanDefinition beanDefinition = registry.beanDefinitionMap.get(beanName);

        //2.初始化bean对象
        Object instance = instantiateBean(beanName, beanDefinition);

        if (instance == null) {
            throw new Exception( beanName + " is not instance");
        }

        //3. 将实例化后的bean封装成BeanWrapper
        MiniBeanWrapper beanWrapper = new MiniBeanWrapper(instance);

        //4. 将beanwrapper保存到ioc容器中
        factoryBeanInstanceCache.put(beanName, beanWrapper);
        
        //5. 依赖注入
        populateBean(beanWrapper);

        return beanWrapper.getWrapperInstance();
    }

    /**
     * 进行依赖注入
     *
     * @param beanWrapper
     */
    private void populateBean(MiniBeanWrapper beanWrapper) {

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
     * 实例化Bean对象（这里需要进行aop）
     *
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

                //========================Aop============================
                //初始化对应的aop支持类，并将本次循环的类和实例对象设置进去，便于后续使用
                MiniAdviceSupport adviceSupport = instantionAopConfig();
                adviceSupport.setTargetClass(beanClass);
                adviceSupport.setTarget(instance);
                //看看这个类和我们定义的切入表达式上的类是否匹配，如果匹配生成代理对象
                if(adviceSupport.pointCoutMatch()) {
                    instance = new MiniJdkDynamicAopProxy(adviceSupport).getProxy();
                }
                //========================Aop============================

                //缓存没有装饰过的，原始的bean对象
                this.factoryBeanObjectCache.put(beanName, instance);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }

    /**
     * 创建Aopconfig对象 和 Aop的支持类
     *
     * @return
     */
    private MiniAdviceSupport instantionAopConfig() {
        MiniAopConfig aopConfig = new MiniAopConfig();
        aopConfig.setPointCut(this.beanDefinitionReader.getContextConfig().getProperty("pointCut"));
        aopConfig.setAspectBefore(this.beanDefinitionReader.getContextConfig().getProperty("aspectBefore"));
        aopConfig.setAspectAfter(this.beanDefinitionReader.getContextConfig().getProperty("aspectAfter"));
        aopConfig.setAspectAfterThrows(this.beanDefinitionReader.getContextConfig().getProperty("aspectAfterThrows"));
        aopConfig.setAspectAfterThrowingName(this.beanDefinitionReader.getContextConfig().getProperty("aspectAfterThrowingName"));
        aopConfig.setAspectClass(this.beanDefinitionReader.getContextConfig().getProperty("aspectClass"));

        return new MiniAdviceSupport(aopConfig);
    }

    @Override
    public Object getBean(Class beanClass) throws Exception {
        return getBean(beanClass.getName());
    }

    /**
     * 获取所有BeanDefinition的数量
     *
     * @return
     */
    public int getBeanDefinitionCount() {
        return registry.beanDefinitionMap.size();
    }

    /**
     * 获取beanDefinition容器中的所有类的类名，并通过String[]返回
     *
     * @return
     */
    public String[] getBeanDefinitionNames() {
        return registry.beanDefinitionMap.keySet().toArray(new String[registry.beanDefinitionMap.size()]);
    }

    public Properties getConfig() {
        return beanDefinitionReader.getContextConfig();
    }
}
