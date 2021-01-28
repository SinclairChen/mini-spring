package com.sinclair.spring.demo.service;

/**
 * @Description 业务的增删改
 * @Date 2021/1/28 12:07
 * @Author by Saiyong.Chen
 */
public interface IModifyService {
    /**
     * 新增
     *
     * @param name
     * @param addr
     * @return
     */
    public String add(String name, String addr);

    /**
     * 根据id修改name
     *
     * @param id
     * @param name
     * @return
     */
    public String edit(Integer id, String name);

    /**
     * 根据id删除
     *
     * @param id
     * @return
     */
    public String remove(Integer id);
}
