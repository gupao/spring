package spring.demo.aspect;

import lombok.extern.slf4j.Slf4j;
import spring.framework.aop.aspect.GPJoinPoint;

import java.util.Arrays;

/**
 * created by xuyahui on 2019/5/19
 */
@Slf4j
public class LogAspect {

    public void before(GPJoinPoint joinPoint){
        // 记录方法调用的开始时间
        joinPoint.setUserAttribute("startTime_" + joinPoint.getMethod().getName(),System.currentTimeMillis());
        //这个方法中的逻辑，是由我们自己写的
        log.info("Invoker Before Method!!!" +
                "\nTargetObject:" +  joinPoint.getThis() +
                "\nArgs:" + Arrays.toString(joinPoint.getArguments()));
    }

    public void after(GPJoinPoint joinPoint){
        // 方式执行时间 = 当前时间-开始时间
        // 检测方法执行的性能
        log.info("Invoker After Method!!!" +
                "\nTargetObject:" +  joinPoint.getThis() +
                "\nArgs:" + Arrays.toString(joinPoint.getArguments()));
        long startTime = (Long) joinPoint.getUserAttribute("startTime_" + joinPoint.getMethod().getName());
        long endTime = System.currentTimeMillis();
        System.out.println("use time :" + (endTime - startTime));
    }

    public void afterThrowing(GPJoinPoint joinPoint, Throwable ex){
        // 异常检测，可以拿到异常信息
        log.info("出现异常" +
                "\nTargetObject:" +  joinPoint.getThis() +
                "\nArgs:" + Arrays.toString(joinPoint.getArguments()) +
                "\nThrows:" + ex.getMessage());
    }


}
