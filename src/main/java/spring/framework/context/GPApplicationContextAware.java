package spring.framework.context;

/**
 * 通过解耦的方式获得IOC容器的顶层设计
 *
 * 后面将通过一个监听器去扫描所有的类，只要实现了此接口，
 * 将自动调用setApplicationContext() 方法，从而将IOC容器注入到目标类中
 *
 * created by xuyahui on 2019/4/10
 */
public interface GPApplicationContextAware {

    void setApplicationContext(GPApplicationContext applicationContext);

}
