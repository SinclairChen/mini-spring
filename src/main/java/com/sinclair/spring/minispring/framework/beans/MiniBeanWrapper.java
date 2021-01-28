package com.sinclair.spring.minispring.framework.beans;

/**
 * @Description TODO
 * @Date 2021/1/28 15:00
 * @Author by Saiyong.Chen
 */
public class MiniBeanWrapper {

    private Object wrapperInstance;

    private Class<?> wrapperClass;

    public MiniBeanWrapper(Object instance) {

        this.wrapperInstance = instance;
        this.wrapperClass = instance.getClass();
    }

    public Object getWrapperInstance() {
        return wrapperInstance;
    }

    public void setWrapperInstance(Object wrapperInstance) {
        this.wrapperInstance = wrapperInstance;
    }

    public Class<?> getWrapperClass() {
        return wrapperClass;
    }

    public void setWrapperClass(Class<?> wrapperClass) {
        this.wrapperClass = wrapperClass;
    }
}
