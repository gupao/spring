package spring.framework.aop.aspect;

import java.lang.reflect.Method;

/**
 * created by xuyahui on 2019/5/28
 */
public interface GPJoinPoint {

    Object getThis();

    Object[] getArguments();

    Method getMethod();

    void setUserAttribute(String key, Object value);

    Object getUserAttribute(String key);
}
