package spring.framework.aop;

/**
 * created by xuyahui on 2019/5/19
 */
public interface GPAopProxy {

    Object getProxy();

    Object getProxy(ClassLoader classLoader);

}
