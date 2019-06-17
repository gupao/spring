package spring.framework.beans;

/**
 * 单例工厂的顶层设计
 * 容器的规范
 * created by xuyahui on 2019/4/10
 */
public interface GPBeanFactory {

    /**
     * 根据BeanName从IOC容器获得一个实例Bean
     *
     * 如果Bean配置了懒加载，那么只有当调用getBean方法的时候，才初始化Bean
     * 如果没有配置懒加载，那么IOC容器初始化的时候就会初始化Bean
     *
     * @param beanName
     * @return
     *
     */
    Object getBean(String beanName) throws Exception;

    Object getBean(Class<?> beanClass) throws Exception;

}
