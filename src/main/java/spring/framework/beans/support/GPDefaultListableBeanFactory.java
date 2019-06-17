package spring.framework.beans.support;

import spring.framework.beans.config.GPBeanDefinition;
import spring.framework.context.support.GPAbstractApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认的容器实现
 *
 * created by xuyahui on 2019/4/14
 */
public class GPDefaultListableBeanFactory extends GPAbstractApplicationContext {

    /**
     * 伪IOC容器
     */
    protected final Map<String,GPBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);

}
