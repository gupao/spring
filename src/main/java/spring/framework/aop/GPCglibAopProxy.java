package spring.framework.aop;

import spring.framework.aop.support.GPAdvisedSupport;

/**
 * created by xuyahui on 2019/5/19
 */
public class GPCglibAopProxy implements GPAopProxy{
    public GPCglibAopProxy(GPAdvisedSupport config) {

    }

    @Override
    public Object getProxy() {
        return null;
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return null;
    }
}
