package com.sinclair.spring.demo.service;

/**
 * @Description 业务查询
 * @Date 2021/1/28 12:08
 * @Author by Saiyong.Chen
 */
public interface IQueryService {
    /**
     * 根据name查询
     * @param name
     * @return
     */
    public String query(String name);
}
