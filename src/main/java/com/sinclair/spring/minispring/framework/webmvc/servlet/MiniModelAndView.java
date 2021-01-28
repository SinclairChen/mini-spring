package com.sinclair.spring.minispring.framework.webmvc.servlet;

import java.util.Map;

/**
 * @Description TODO
 * @Date 2021/1/28 17:51
 * @Author by Saiyong.Chen
 */
public class MiniModelAndView {

    private String viewName;

    private Map<String, ?> model;

    public MiniModelAndView(String viewName, Map<String, ?> model) {
        this.viewName = viewName;
        this.model = model;
    }

    public MiniModelAndView(String viewName) {
        this.viewName = viewName;
    }

    public String getViewName() {
        return viewName;
    }

    public Map<String, ?> getModel() {
        return model;
    }
}
