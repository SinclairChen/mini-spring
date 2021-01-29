package com.sinclair.spring.minispring.framework.aop.aspect;

import lombok.Data;

import java.lang.reflect.Method;

/**
 * @Description 封装织入类
 * @Date 2021/1/29 7:04
 * @Author by Saiyong.Chen
 */
@Data
public class MiniAdvice {

    /** 保存织入的对象 */
    private Object aspect;

    /** 添加需要增强的方法 */
    private Method adviceMethod;

    /** 保持异常的名字 */
    private String throwName;

    public MiniAdvice(Object newInstance, Method adviceMethod) {
        this.aspect = newInstance;
        this.adviceMethod = adviceMethod;
    }
}
