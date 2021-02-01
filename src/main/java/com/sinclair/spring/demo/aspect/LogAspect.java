package com.sinclair.spring.demo.aspect;

import com.sinclair.spring.minispring.framework.aop.aspect.MiniJoinPoint;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description 日志切面类
 * @Date 2021/1/29 6:50
 * @Author by Saiyong.Chen
 */
@Slf4j
public class LogAspect {

    /**
     * 在调用一个方法之前，执行before方法
     */
    public void before(MiniJoinPoint joinPoint){
        log.info("Invoker Before Method!!!");
    }

    /**
     * 在调用一个方法之后，执行after方法
     */
    public void after(MiniJoinPoint joinPoint){
        log.info("Invoker After Method!!!");
    }

    /**
     * 出现异常后执行的方法
     */
    public void afterThrowing(MiniJoinPoint joinPoint, Throwable exception){

        log.info("出现异常");
    }
}
