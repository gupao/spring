package spring.framework.context.support;

/**
 * IOC 容器实现的顶层设计
 *
 * created by xuyahui on 2019/4/14
 */
public abstract class GPAbstractApplicationContext {

    /**
     * 受保护的，只提供给子类重写
     */
    protected void refresh(){}

}
