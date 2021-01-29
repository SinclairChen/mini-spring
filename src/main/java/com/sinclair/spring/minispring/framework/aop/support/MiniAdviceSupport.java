package com.sinclair.spring.minispring.framework.aop.support;

import com.sinclair.spring.minispring.framework.aop.aspect.MiniAdvice;
import com.sinclair.spring.minispring.framework.aop.config.MiniAopConfig;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * @Description 负责解析aop的配置文件
 * @Date 2021/1/29 7:08
 * @Author by Saiyong.Chen
 */
public class MiniAdviceSupport {

    private MiniAopConfig aopConfig;
    private Class<?> beanClass;
    private Object instance;

    private Pattern pointCutClassPattern;

    private Map<Method, Map<String, MiniAdvice>> methodCache;


    public MiniAdviceSupport(MiniAopConfig aopConfig) {
        this.aopConfig = aopConfig;
    }

    /**
     * 通过方法，获取该方法上的所有织入方法
     *
     * @param method
     * @param o
     * @return
     * @throws NoSuchMethodException
     */
    public Map<String, MiniAdvice> getAdvice(Method method, Object o) throws NoSuchMethodException {
        Map<String, MiniAdvice> adviceMap = methodCache.get(method);

        //如果没有，放入
        if (adviceMap == null) {
            Method m = beanClass.getMethod(method.getName(), method.getParameterTypes());

            //
            adviceMap = null;

            this.methodCache.put(m, adviceMap);
        }

        return adviceMap;
    }


    /**
     * 判断实例化的这个对象是否需要通过aop增强
     *
     * @return
     */
    public boolean pointCoutMatch() {

        return this.pointCutClassPattern.matcher(this.beanClass.toString()).matches();
    }


    /**
     * 设置目标对象 class对象 并 解析
     * @param beanClass
     */
    public void setTargetClass(Class<?> beanClass) {
        this.beanClass = beanClass;
        //解析
        doParse();
    }

    /**
     * 解析配置文件
     */
    private void doParse() {
        //1. 解析切面表达式
        String pointCut = aopConfig.getPointCut()
                .replaceAll("\\.","\\\\.")
                .replaceAll("\\\\.\\*",".*")
                .replaceAll("\\(","\\\\(")
                .replaceAll("\\)","\\\\)");
        Pattern pointCutPattern = Pattern.compile(pointCut);

        methodCache = new HashMap<Method, Map<String, MiniAdvice>>();


        try {
            //2. 拿到织入对象，获取before、 after之类的织入的方法并保存起来，便于后续处理
            Class<?> aspectClass = Class.forName(this.aopConfig.getAspectClass());
            Map<String, Method> aspectMethods = new HashMap<>();
            for (Method method : aspectClass.getMethods()) {
                aspectMethods.put(method.getName(), method);
            }

            //3. 封装MiniAdvice
            for (Method method : this.beanClass.getMethods()) {
                String methodString = method.toString();

                //处理声明式异常
                if (methodString.contains("thorws")) {
                    methodString = methodString.substring(0, methodString.lastIndexOf("throws")).trim();
                }

                // 将实例化的bean的方法 和 配置的切入点表达式匹配一下
                // 如果匹配，创建MiniAdvice并先存起来
                Matcher matcher = pointCutPattern.matcher(methodString);

                if (matcher.matches()) {
                    Map<String, MiniAdvice> adviceMap = new HashMap<>();

                    if (!(aopConfig.getAspectBefore() == null || "".equals(aopConfig.getAspectBefore()))) {
                        adviceMap.put("before", new MiniAdvice(aspectClass.newInstance(), aspectMethods.get(aopConfig.getAspectBefore())));
                    }
                    if(!(null == aopConfig.getAspectAfter() || "".equals(aopConfig.getAspectAfter()))){
                        adviceMap.put("after",new MiniAdvice(aspectClass.newInstance(),aspectMethods.get(aopConfig.getAspectAfter())));
                    }
                    if(!(null == aopConfig.getAspectAfterThrows() || "".equals(aopConfig.getAspectAfterThrows()))){
                        MiniAdvice advice = new MiniAdvice(aspectClass.newInstance(),aspectMethods.get(aopConfig.getAspectAfterThrows()));
                        advice.setThrowName(aopConfig.getAspectAfterThrowingName());
                        adviceMap.put("afterThrow",advice);
                    }

                    // 最后将目标方法和织入的方法关联起来，便于在动态代理类中使用
                    methodCache.put(method,adviceMap);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        // 4、将切入点的 全类名 用正则保存起来，后续可以通过这个匹配判断是否需要对这个类中的方法进行增强
        String pointCoutForClassRegex = pointCut.substring(0, pointCut.lastIndexOf("\\(") - 4);
        pointCutClassPattern = Pattern.compile("class " + pointCoutForClassRegex.lastIndexOf(" ") + 1);

    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public void setTarget(Object instance) {
        this.instance = instance;
    }

    public Object getTarget() {
        return instance;
    }
}
