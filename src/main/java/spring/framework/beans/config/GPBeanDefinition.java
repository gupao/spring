package spring.framework.beans.config;

import lombok.Data;

/**
 * 源码中这个类是接口
 * created by xuyahui on 2019/4/10
 */
@Data
public class GPBeanDefinition {

    private String beanClassName;
    private boolean lazyInit = false;
    private String factoryBeanName;
    private boolean isSingleton = true;


}
