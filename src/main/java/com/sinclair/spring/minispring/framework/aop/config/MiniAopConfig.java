package com.sinclair.spring.minispring.framework.aop.config;

import lombok.Data;

/**
 * @Description Aop配置的定义类
 * @Date 2021/1/29 7:02
 * @Author by Saiyong.Chen
 */
@Data
public class MiniAopConfig {

    private String pointCut;

    private String aspectClass;

    private String aspectBefore;

    private String aspectAfter;

    private String aspectAfterThrows;

    private String aspectAfterThrowingName;


}
