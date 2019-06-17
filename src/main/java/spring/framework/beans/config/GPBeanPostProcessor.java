package spring.framework.beans.config;

/**
 * 模拟spring中的事件传播器
 * created by xuyahui on 2019/4/23
 */
public class GPBeanPostProcessor {

    /**
     * bean 初始化之前做的事情
     * @param bean
     * @param beanName
     * @return
     * @throws Exception
     */
    public Object postProcessBeforeInitialization(Object bean,String beanName) throws Exception{
        return bean;
    };

    /**
     * bean 初始化之后做的事情
     * @param bean
     * @param beanName
     * @return
     * @throws Exception
     */
    public Object postProcessAfterInitialization(Object bean,String beanName) throws Exception{
        return bean;
    }

}
