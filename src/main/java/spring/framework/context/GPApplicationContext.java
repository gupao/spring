package spring.framework.context;

import spring.framework.annotation.GPAutowired;
import spring.framework.annotation.GPController;
import spring.framework.annotation.GPService;
import spring.framework.aop.GPAopProxy;
import spring.framework.aop.GPCglibAopProxy;
import spring.framework.aop.GPJdkDynamicAopProxy;
import spring.framework.aop.config.GPAopConfig;
import spring.framework.aop.support.GPAdvisedSupport;
import spring.framework.beans.GPBeanFactory;
import spring.framework.beans.GPBeanWrapper;
import spring.framework.beans.config.GPBeanDefinition;
import spring.framework.beans.config.GPBeanPostProcessor;
import spring.framework.beans.support.GPBeanDefinitionReader;
import spring.framework.beans.support.GPDefaultListableBeanFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 最底层的容器的实现
 *
 * created by xuyahui on 2019/4/10
 */
public class GPApplicationContext extends GPDefaultListableBeanFactory implements GPBeanFactory {

    private String[] configLocations;

    private GPBeanDefinitionReader reader;

    /**
     * 单例的IOC容器缓存
     */
    private Map<String,Object> factoryBeanObjectCache = new ConcurrentHashMap<>();

    /**
     * 通用的IOC缓存
     */
    private Map<String,GPBeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<String,GPBeanWrapper>();

    public GPApplicationContext(String... configLoactions){
        this.configLocations = configLoactions;
        refresh();
    }

    @Override
    public void refresh(){
        try {
            // 1.定位：定位配置文件
            reader = new GPBeanDefinitionReader(this.configLocations);

            // 2.加载配置文件：扫码相关的类，把他们封装成BeanDefinition
            List<GPBeanDefinition> beanDefinitions = reader.loadBeanDefinitions();

            // 3.注册:把配置信息放到容器里面（伪IOC容器）
            doRegisterBeanDefinition(beanDefinitions);

            // 4.把不是延迟加载的类，提前初始化
            doAutoWrited();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 只处理非延时加的bean
     */
    private void doAutoWrited() throws Exception {
        for (Map.Entry<String, GPBeanDefinition> beanDefinitionEntry : beanDefinitionMap.entrySet()) {
            if(!beanDefinitionEntry.getValue().isLazyInit()){
                getBean(beanDefinitionEntry.getKey());
            }
        }
    }

    @Override
    public Object getBean(Class<?> beanClass) throws Exception{
        return getBean(beanClass.getName());
    }

    @Override
    public Object getBean(String beanName) throws Exception {

        // 1.初始化
        GPBeanWrapper beanWrapper = instantiateBean(beanName,this.beanDefinitionMap.get(beanName));

        // 工厂模式+策略模式
//        GPBeanPostProcessor beanPostProcessor = new GPBeanPostProcessor();
//        beanPostProcessor.postProcessBeforeInitialization(instance,beanName);// 初始化之前做的事情

        // 将实例化的对象封装到beanWrapper
//        GPBeanWrapper beanWrapper = new GPBeanWrapper(instance);

        // 2.拿到beanwrapper之后，存到IOC容器中去
//        if(this.factoryBeanInstanceCache.containsKey(beanName)){
//            throw new Exception("the " + beanName+ " is exists!");
//        }
        this.factoryBeanInstanceCache.put(beanName,beanWrapper);


//        beanPostProcessor.postProcessAfterInitialization(instance,beanName);// 初始化之后做的事情


        // 3.注入
        populateBean(beanName,new GPBeanDefinition(),beanWrapper);


        return this.factoryBeanInstanceCache.get(beanName).getWrappedInstance();
    }

    /**
     * 注入
     * @param beanName
     * @param gpBeanDefinition
     */
    private void populateBean(String beanName, GPBeanDefinition gpBeanDefinition,GPBeanWrapper beanWrapper) {
        Object instance = beanWrapper.getWrappedInstance();
        Class<?> clazz = beanWrapper.getWrappedClass();
        if(!(clazz.isAnnotationPresent(GPController.class) || clazz.isAnnotationPresent(GPService.class))){
            return;
        }

        Field[] fields =  clazz.getDeclaredFields();
        for (Field field : fields) {
            if(!field.isAnnotationPresent(GPAutowired.class)){continue;}

            GPAutowired autowired = field.getAnnotation(GPAutowired.class);
            String autowiredBeanName = autowired.value().trim();
            if("".equals(autowiredBeanName)){
                autowiredBeanName = field.getType().getName();
            }
            field.setAccessible(true);
            try {
                if(this.factoryBeanInstanceCache.get(autowiredBeanName) == null){ continue; }
                field.set(instance,this.factoryBeanInstanceCache.get(autowiredBeanName).getWrappedInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 初始化Bean
     * @param beanName
     * @param gpBeanDefinition
     */
    private GPBeanWrapper instantiateBean(String beanName, GPBeanDefinition gpBeanDefinition) {
        // 1.拿到要实例化的类名
        String className = gpBeanDefinition.getBeanClassName();

        // 2.根据类名，实例化对象
        Object instance = null;
        try {
            if(this.factoryBeanObjectCache.containsKey(className)){
                instance = this.factoryBeanObjectCache.get(className);
            }else {
                Class clazz = Class.forName(className);
                instance = clazz.newInstance();

                GPAdvisedSupport config = instantionAopConfig(gpBeanDefinition);
                config.setTarget(instance);
                config.setTargetClass(clazz);
                // 如果符合pointCut规则的话，就创建对象;否则就使用原生的对象
                if(config.pointCutMatch()){
                    instance = createProxy(config).getProxy();
                }
//                instance = clazz.newInstance();
                factoryBeanObjectCache.put(className,instance);
                factoryBeanObjectCache.put(gpBeanDefinition.getFactoryBeanName(),instance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 3.将实例化的对象封装到beanWrapper
        GPBeanWrapper beanWrapper = new GPBeanWrapper(instance);

        return beanWrapper;
    }

    private GPAopProxy createProxy(GPAdvisedSupport config) {
        Class clazz = config.getTargetClass();
        if(clazz.getInterfaces().length > 0){
            return new GPJdkDynamicAopProxy(config);
        }
        return new GPCglibAopProxy(config);
    }

    private GPAdvisedSupport instantionAopConfig(GPBeanDefinition gpBeanDefinition) {
        GPAopConfig config = new GPAopConfig();
        config.setPointCut(this.reader.getConfig().getProperty("pointCut"));
        config.setAspectClass(this.reader.getConfig().getProperty("aspectClass"));
        config.setAspectBefore(this.reader.getConfig().getProperty("aspectBefore"));
        config.setAspectAfter(this.reader.getConfig().getProperty("aspectAfter"));
        config.setAspectAfterThrow(this.reader.getConfig().getProperty("aspectAfterThrow"));
        config.setAspectAfterThrowingName(this.reader.getConfig().getProperty("aspectAfterThrowingName"));
        return new GPAdvisedSupport(config);
    }


    private void doRegisterBeanDefinition(List<GPBeanDefinition> beanDefinitions) {
        for (GPBeanDefinition beanDefinition : beanDefinitions) {
            super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(),beanDefinition);
        }
    }


    public String[] getBeanDefinitionNames(){
        return this.beanDefinitionMap.keySet().toArray(new String[this.beanDefinitionMap.size()]);
    }

    public int getBeanDefinitionCount(){
        return this.beanDefinitionMap.size();
    }

    public Properties getConfig(){
        return this.reader.getConfig();
    }


}
