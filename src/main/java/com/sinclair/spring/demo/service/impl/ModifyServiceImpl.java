package com.sinclair.spring.demo.service.impl;

import com.sinclair.spring.demo.service.IModifyService;
import com.sinclair.spring.minispring.framework.annotation.MiniService;

/**
 * @Description 增删改业务 模拟 处理
 * @Date 2021/1/28 12:11
 * @Author by Saiyong.Chen
 */
@MiniService
public class ModifyServiceImpl implements IModifyService {

    @Override
    public String add(String name, String addr) {
        return "modifyService add,name=" + name + ",addr=" + addr;
    }

    @Override
    public String edit(Integer id, String name) {
        return "modifyService edit,id=" + id + ",name=" + name;
    }

    @Override
    public String remove(Integer id) {
        return "modifyService id=" + id;
    }
}
