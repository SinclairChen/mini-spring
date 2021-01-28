package com.sinclair.spring.minispring.framework.webmvc.servlet;

import com.sinclair.spring.minispring.framework.context.MiniApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 * @Description 委派：任务调度和请求分发
 * @Date 2021/1/28 12:35
 * @Author by Saiyong.Chen
 */
public class MiniDispatcherServlet extends HttpServlet {

    private MiniApplicationContext applicationContext;

    // 初始化
    @Override
    public void init(ServletConfig config) throws ServletException {

        //1.加载配置文件，初始化核心ioc容器
        applicationContext = new MiniApplicationContext(config.getInitParameter("contextConfigLocation"));



    }
}
