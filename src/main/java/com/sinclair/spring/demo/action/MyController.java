package com.sinclair.spring.demo.action;

import com.sinclair.spring.demo.service.IModifyService;
import com.sinclair.spring.demo.service.IQueryService;
import com.sinclair.spring.minispring.framework.annotation.MiniAutoWried;
import com.sinclair.spring.minispring.framework.annotation.MiniController;
import com.sinclair.spring.minispring.framework.annotation.MiniRequestMapping;
import com.sinclair.spring.minispring.framework.annotation.MiniRequestParam;
import com.sinclair.spring.minispring.framework.webmvc.servlet.MiniModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * @Description 开放接口
 * @Date 2021/1/28 12:18
 * @Author by Saiyong.Chen
 */
@MiniController
@MiniRequestMapping("/web")
public class MyController {

    @MiniAutoWried
    IQueryService queryService;

    @MiniAutoWried
    IModifyService modifyService;

    @MiniRequestMapping("/query.json")
    public MiniModelAndView query(HttpServletRequest request, HttpServletResponse response, @MiniRequestParam String name) {
        String query = queryService.query(name);
        return writeTo(response, query);
    }

    @MiniRequestMapping("/add*.json")
    public MiniModelAndView add(HttpServletRequest request,HttpServletResponse response,
                    @MiniRequestParam("name") String name,@MiniRequestParam("addr") String addr){
        String result = modifyService.add(name,addr);
        return writeTo(response,result);
    }

    @MiniRequestMapping("/remove.json")
    public MiniModelAndView remove(HttpServletRequest request,HttpServletResponse response,
                       @MiniRequestParam("id") Integer id){
        String result = modifyService.remove(id);
        return writeTo(response,result);
    }

    @MiniRequestMapping("/edit.json")
    public MiniModelAndView edit(HttpServletRequest request,HttpServletResponse response,
                     @MiniRequestParam("id") Integer id,
                     @MiniRequestParam("name") String name){
        String result = modifyService.edit(id,name);
        return writeTo(response,result);
    }

    /**
     * 向页面返回数据
     * @param response
     * @param str
     */
    public MiniModelAndView writeTo(HttpServletResponse response, String str) {
        try {
            response.getWriter().write(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
