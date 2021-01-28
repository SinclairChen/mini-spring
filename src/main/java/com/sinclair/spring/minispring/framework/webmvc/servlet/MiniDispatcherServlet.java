package com.sinclair.spring.minispring.framework.webmvc.servlet;

import com.sinclair.spring.minispring.framework.annotation.MiniController;
import com.sinclair.spring.minispring.framework.annotation.MiniRequestMapping;
import com.sinclair.spring.minispring.framework.context.MiniApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description 委派：任务调度和请求分发
 * @Date 2021/1/28 12:35
 * @Author by Saiyong.Chen
 */
public class MiniDispatcherServlet extends HttpServlet {

    private MiniApplicationContext applicationContext;

    private List<MiniHandlerMapping> handlerMappings = new ArrayList<MiniHandlerMapping>();

    private Map<MiniHandlerMapping, MiniHandlerAdapter> handlerAdapters = new HashMap<MiniHandlerMapping,MiniHandlerAdapter>();

    private List<MiniViewResolver> viewResolvers = new ArrayList<MiniViewResolver>();

    // 初始化
    @Override
    public void init(ServletConfig config) throws ServletException {

        //1.加载配置文件，初始化核心ioc容器
        this.applicationContext = new MiniApplicationContext(config.getInitParameter("contextConfigLocation"));

        //2. 初始化mvc的九大组件
        initStrategies(applicationContext);

    }

    /**
     * 初始化mvc的各个组件
     * @param applicationContext
     */
    private void initStrategies(MiniApplicationContext applicationContext) {

//        初始化多文件上传组件
//        initMultipartResolver(applicationContext);
//        初始化本地语言环境
//        initLocalResovler(applicationContext);
//        初始化模板处理器
//        initThemeResolver(applicationContext);

        //初始化HandlerMapping
        initHandlerMappings(applicationContext);

        //初始化HandlerAdapter
        initHandlerAdapters(applicationContext);

//        初始化异常处理器
//        initHandlerExceptionResolver(applicationContext);
//        初始化视图预处理器
//        initRequestToViewNameTranslator(applicationContext);

        //初始化视图处理器
        initViewResolvers(applicationContext);

//        初始化FlashMap管理器
//        initFlashMapManager(applicationContext);
    }

    /**
     * 初始化视图处理器
     *
     * @param applicationContext
     */
    private void initViewResolvers(MiniApplicationContext applicationContext) {
        String templatePath = applicationContext.getConfig().getProperty("templatePath");
        String templateRootPath = this.getClass().getClassLoader().getResource(templatePath).getFile();
        File templateRootDir = new File(templateRootPath);
        for (File file : templateRootDir.listFiles()) {
            this.viewResolvers.add(new MiniViewResolver(templatePath));
        }

    }

    /**
     * 初始化适配器
     *
     * @param applicationContext
     */
    private void initHandlerAdapters(MiniApplicationContext applicationContext) {
        for (MiniHandlerMapping handlerMapping : handlerMappings) {
            this.handlerAdapters.put(handlerMapping, new MiniHandlerAdapter());
        }
    }

    /**
     * 初始化HandlerMapping
     *
     * @param applicationContext
     */
    private void initHandlerMappings(MiniApplicationContext applicationContext) {
        if(applicationContext.getBeanDefinitionCount() == 0) {
            return;
        }

        try {
            for(String beanName : applicationContext.getBeanDefinitionNames()) {
                Class<?> beanClass = applicationContext.getBean(beanName).getClass();

                //如果不是controller 不需要
                if (!beanClass.isAnnotationPresent(MiniController.class)) {
                    continue;
                }

                //获取类上的url
                String baseUrl = "";
                if (beanClass.isAnnotationPresent(MiniRequestMapping.class)) {
                    baseUrl = beanClass.getAnnotation(MiniRequestMapping.class).value();
                }

                //获取方法上的url
                for (Method method : beanClass.getMethods()) {
                    if (!method.isAnnotationPresent(MiniRequestMapping.class)) {
                        continue;
                    }
                    String methodUrl = method.getAnnotation(MiniRequestMapping.class).value();

                    String regex = ("/" + baseUrl + "/" + methodUrl.replaceAll("\\*", ".*")).replace("/+","/");
                    Pattern pattern = Pattern.compile(regex);
                    //创建HandlerMapping 并添加到 容器中
                    handlerMappings.add(new MiniHandlerMapping(pattern, method, beanClass.newInstance()));
                    System.out.println("HandlerMapping : " + regex + " , " + method);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {

            doDispatch(req, resp);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 请求分发
     *
     * @param request
     * @param response
     */
    private void doDispatch(HttpServletRequest request, HttpServletResponse response) throws InvocationTargetException, IllegalAccessException {


        // 1. 通过url获取handlerMapping
        MiniHandlerMapping handlerMapping = getHandler(request);
        //如果没有直接返回404
        if (handlerMapping == null) {
            processDispatchResult(request, response, new MiniModelAndView("404 Not Found!"));
            return;
        }

        //2. 通过handlerMapping 获取 handlerAdapter
        MiniHandlerAdapter handlerAdapter = getHandlerAdapter(handlerMapping);

        //3. 通过适配器处理对应的handler 返回封装后的ModelAndView
        MiniModelAndView modelAndView = handlerAdapter.handler(request, response, handlerMapping);

        //4. 将modelAndView 转换成 viewResolver
        processDispatchResult(request, response, modelAndView);
    }

    /**
     * 根据handlerMapping 获取 对应的适配器
     *
     * @param handlerMapping
     */
    private MiniHandlerAdapter getHandlerAdapter(MiniHandlerMapping handlerMapping) {

        if (this.handlerAdapters.isEmpty()) {
            return null;
        }
        return this.handlerAdapters.get(handlerMapping);
    }


    /**
     * 将modelAndView 通过 viewResolver 转换成 view 并返回
     *
     * @param request
     * @param response
     * @param modelAndView
     */
    private void processDispatchResult(HttpServletRequest request, HttpServletResponse response, MiniModelAndView modelAndView) {
        if (modelAndView == null) { return; }
        if (this.viewResolvers.isEmpty()) { return; }

        for (MiniViewResolver viewResolver : this.viewResolvers) {
            MiniView view = viewResolver.resolveViewName(modelAndView.getViewName());
            view.render(request, response, modelAndView.getModel());
            return;
        }

    }


    /**
     * 通过URL获取对应的handlerMapping
     *
     * @param request
     * @return
     */
    private MiniHandlerMapping getHandler(HttpServletRequest request) {

        if (handlerMappings.isEmpty()) {
            return null;
        }
        //获取url
        String url = request.getRequestURI();
        String contextPath = request.getContextPath();
        url = url.replaceAll(contextPath, "").replaceAll("/+", "/");

        //循环匹配所有的handlerMapping，并返回
        for (MiniHandlerMapping handlerMapping : handlerMappings) {
            Matcher matcher = handlerMapping.getPattern().matcher(url);
            if (!matcher.matches()) {
                continue;
            }
            return handlerMapping;
        }
        return null;
    }
}
