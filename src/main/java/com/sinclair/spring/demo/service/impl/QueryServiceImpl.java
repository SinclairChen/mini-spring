package com.sinclair.spring.demo.service.impl;

import com.sinclair.spring.demo.service.IQueryService;
import com.sinclair.spring.minispring.framework.annotation.MiniService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 * @Description 查询业务 模拟 处理
 * @Date 2021/1/28 12:12
 * @Author by Saiyong.Chen
 */
@MiniService
public class QueryServiceImpl implements IQueryService {
    @Override
    public String query(String name) {
        String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return "{name:\"" + name + "\",time:\"" + dateTime + "\"}";
    }
}
