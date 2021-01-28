package com.sinclair.spring.minispring.framework.webmvc.servlet;

import java.io.File;

/**
 * @Description 视图处理器
 * @Date 2021/1/28 17:51
 * @Author by Saiyong.Chen
 */
public class MiniViewResolver {

    private final String DEFAULT_TEMPLATE_SUFFIX = ".html";

    private File templateRootDir;

    public MiniViewResolver(String templateRoot) {
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();

        templateRootDir = new File(templateRootPath);
    }

    public MiniView resolveViewName(String viewName) {
        if(null == viewName || "".equals(viewName.trim())){
            return null;
        }
        viewName = viewName.endsWith(DEFAULT_TEMPLATE_SUFFIX) ? viewName : viewName + DEFAULT_TEMPLATE_SUFFIX;
        File templateFile = new File((templateRootDir.getPath() + "/" + viewName).replaceAll("/+", "/"));
        return new MiniView(templateFile);
    }
}
