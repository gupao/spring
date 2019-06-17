package spring.framework.beans;

/**
 * created by xuyahui on 2019/4/21
 */
public class GPBeanWrapper {

    private Object wrappedInstance;

    private Class<?> wrappedClass;

    public GPBeanWrapper(Object wrappedInstance) {
        this.wrappedInstance = wrappedInstance;
    }

    /**
     * 是单例，直接获取
     * @return
     */
    public Object getWrappedInstance(){
        return this.wrappedInstance;
    }

    /**
     * 不是单例，new 一个对象
     * @return
     */
    public Class<?> getWrappedClass(){
        return this.wrappedInstance.getClass();
    }


}
