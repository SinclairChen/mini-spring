package com.sinclair.spring.minispring.framework.beans.support;

import com.sinclair.spring.minispring.framework.annotation.MiniController;
import com.sinclair.spring.minispring.framework.annotation.MiniService;
import com.sinclair.spring.minispring.framework.beans.config.MiniBeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @Description 负责读取配置文件并扫描相关的类，将他们保存到内存中
 * @Date 2021/1/28 13:09
 * @Author by Saiyong.Chen
 */
public class MiniBeanDefinitionReader {

    /** 将读取的到的配置文件保存为properties */
    private Properties contextConfig = new Properties();

    /** 用于存储所有扫描到的类的全类名 */
    private List<String> registryBeanClasses = new ArrayList<String>();

    public MiniBeanDefinitionReader(String[] configLocations) {

        //1、硬编码 读取第一个文件
        doLoadConfig(configLocations[0]);

        //2、扫描配置文件中相关的类
        doScanner(contextConfig.getProperty("scanPackage"));
    }


    /**
     * 扫描所有的类，并将类的全类名保存起来
     *
     * @param scanPackage
     */
    public void doScanner(String scanPackage) {

        URL url = this.getClass().getClassLoader().getResource("/" + scanPackage.replaceAll("\\.", "/"));
        File classPath = new File(url.getFile());

        for (File file : classPath.listFiles()) {
            if (file.isDirectory()) {
                doScanner(scanPackage + "." + file.getName());
            } else {
                if (!file.getName().endsWith(".class")) {
                    continue;
                }

                String className = scanPackage + "." + file.getName().replace(".class", "");

                //将扫描的类的全类名，保存起来
                registryBeanClasses.add(className);
            }
        }



    }

    /**
     * 加载配置文件，并解析成properties
     *
     * @param contextConfigLocation
     */
    public void doLoadConfig (String contextConfigLocation) {

        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation.replaceAll("classpath:",""));

        try {
            contextConfig.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 将所有扫描到的类，封装成BeanDefinition
     *
     * @return
     */
    public List<MiniBeanDefinition> loadBeanDefinitions() {

        List<MiniBeanDefinition> beanDefinitionList = new ArrayList<MiniBeanDefinition>();

        try {
            for (String className : registryBeanClasses) {

                Class<?> beanClass = Class.forName(className);

                //判断是否有自定义类名，如果有自定义类名，使用自定义类名（这里只有MiniController和MiniService两种方式）
                String beanName = "";
                if (beanClass.isAnnotationPresent(MiniController.class)) {
                    MiniController controller = beanClass.getAnnotation(MiniController.class);
                    beanName = controller.value();
                }
                if(beanClass.isAnnotationPresent(MiniService.class)) {
                    MiniService service = beanClass.getAnnotation(MiniService.class);
                    beanName = service.value();
                }
                //如果没有自定义类名，获取类的simpleName,并默认首字母小写
                if("".equals(beanName.trim())){
                    beanName = toFirstLowerCase(beanClass.getSimpleName());
                }

                // 创建BeanDefinition 同时将他们保存起来
                beanDefinitionList.add(doCreateBeanDefinition(beanName, beanClass.getName()));

                // 接口注入的类也保存起来
                for (Class<?> classInterface : beanClass.getInterfaces()) {
                    beanDefinitionList.add(doCreateBeanDefinition(classInterface.getName(), beanClass.getName()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return beanDefinitionList;
    }

    /**
     * 创建 BeanDefinition
     * @param beanName
     * @param beanClassName
     * @return
     */
    private MiniBeanDefinition doCreateBeanDefinition(String beanName, String beanClassName) {

        MiniBeanDefinition beanDefinition = new MiniBeanDefinition();
        beanDefinition.setBeanClassName(beanClassName);
        beanDefinition.setFactoryBeanName(beanName);
        return beanDefinition;
    }

    /**
     * 将类名的首字母转换为小写
     * @param simpleName
     * @return
     */
    public String toFirstLowerCase(String simpleName) {
        char[] chars = simpleName.toCharArray();
        if(65 <= chars[0] && chars[0] <= 90 ) {
            chars[0] += 32;
        }
        return String.valueOf(chars);
    }


}
