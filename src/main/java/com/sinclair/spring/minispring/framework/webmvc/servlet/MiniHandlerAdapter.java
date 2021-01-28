package com.sinclair.spring.minispring.framework.webmvc.servlet;

import com.sinclair.spring.minispring.framework.annotation.MiniRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description handler 适配器
 * @Date 2021/1/28 17:51
 * @Author by Saiyong.Chen
 */
public class MiniHandlerAdapter {


    public MiniModelAndView handler(HttpServletRequest request, HttpServletResponse response, MiniHandlerMapping handlerMapping) throws InvocationTargetException, IllegalAccessException {

        //1. 将参数名称 和 参数的位置，一一对应的保存起来，以便后面处理
        Map<String, Integer> paramIndexMap = new HashMap<>();

        Method method = handlerMapping.getMethod();

        //如果参数列表中有 HttpServletRequest和 HttpServletResponse，先处理它们
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            if (parameterType == HttpServletRequest.class || parameterType == HttpServletResponse.class) {
                paramIndexMap.put(parameterType.getName(), i);
            }
        }

        //然后处理其他的参数（加了@MiniRequestParam注解的）
        //这里的二维数组：一维表示参数，二维表示每个参数上的注解
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < parameterAnnotations.length; i++) {
            for (Annotation annotation : parameterAnnotations[i]) {
                //循环判读每一个参数上的注解是不是@MiniRequestParam
                //判断有没有参数名,如果有保存起来
                if(annotation instanceof MiniRequestParam) {
                    String paramName = ((MiniRequestParam) annotation).value();
                    if (!"".equals(paramName.trim())) {
                        paramIndexMap.put(paramName, i);
                    }
                }
            }
        }


        //2. 拿到实参，并拼接
        Map<String, String[]> params = request.getParameterMap();

        //将所有参数按照一定的位置保存在这个数组中
        Object[] paramValues = new Object[parameterTypes.length];

        //如果有HttpServletRequest和HttpServletResponse，先处理
        if (paramIndexMap.containsKey(HttpServletRequest.class.getName())) {
            Integer index = paramIndexMap.get(HttpServletRequest.class.getName());
            paramValues[index] = request;
        }
        if (paramIndexMap.containsKey(HttpServletResponse.class.getName())) {
            Integer index = paramIndexMap.get(HttpServletResponse.class.getName());
            paramValues[index] = response;
        }


        //然后处理其他参数
        for (Map.Entry<String, String[]> param : params.entrySet()) {
            String value = Arrays.toString(params.get(param.getKey()))
                    .replaceAll("\\[|\\]}", "")
                    .replaceAll("\\s",",");

            //判断有没有这个形参，拿到这个形参的位置，并按照这个位置存到我们的参数数组中的
            if (!paramIndexMap.containsKey(param.getKey())) {
                continue;
            }
            int index = paramIndexMap.get(param.getKey());

            //这里需要将string类型转换成对应的类型
            paramValues[index] = castStringValue(value, parameterTypes[index]);
        }


        //3. 先前的参数处理都是为了在这里方便通过反射调用具体的方法
        Object result = method.invoke(handlerMapping.getControllre(), paramValues);

        //4. 判断返回结果，如果是ModelAndView直接返回
        if (method.getReturnType() == MiniModelAndView.class) {
            return (MiniModelAndView) result;
        }

//        if (result == null || result instanceof Void) {
//            return null;
//        }
//
        return null;
    }

    /**
     * 将string类型转换成对应的类型（硬编码）
     *
     * @param value
     * @param parameterType
     * @return
     */
    private Object castStringValue(String value, Class<?> parameterType) {
        if (String.class == parameterType) {
            return value;
        } else if (Integer.class == parameterType) {
            return Integer.valueOf(value);
        } else if(Double.class == parameterType) {
            return Double.valueOf(value);
        }else {
            if (value != null) {
                return value;
            }
            return null;
        }
    }
}
