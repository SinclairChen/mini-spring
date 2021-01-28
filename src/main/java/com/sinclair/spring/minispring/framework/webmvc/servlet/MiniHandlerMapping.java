package com.sinclair.spring.minispring.framework.webmvc.servlet;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * @Description HandlerMapping
 * @Date 2021/1/28 17:34
 * @Author by Saiyong.Chen
 */
public class MiniHandlerMapping {

    /** url */
    private Pattern pattern;

    /** 对应的方法 */
    private Method method;

    /** 所在的controller */
    private Object controllre;

    public MiniHandlerMapping(Pattern pattern, Method method, Object controllre) {
        this.pattern = pattern;
        this.method = method;
        this.controllre = controllre;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object getControllre() {
        return controllre;
    }

    public void setControllre(Object controllre) {
        this.controllre = controllre;
    }
}
