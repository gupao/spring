package spring.framework.aop;

import spring.framework.aop.intercept.GPMethodInvocation;
import spring.framework.aop.support.GPAdvisedSupport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * created by xuyahui on 2019/5/19
 */
public class GPJdkDynamicAopProxy implements GPAopProxy,InvocationHandler {

    private GPAdvisedSupport advised;

    public GPJdkDynamicAopProxy() {
    }

    public GPJdkDynamicAopProxy(GPAdvisedSupport advised){
        this.advised = advised;
    }

    @Override
    public Object getProxy() {
        return getProxy(this.advised.getClass().getClassLoader());
    }

    /**
     * 通过代理模式，获得代理对象
     * @param classLoader
     * @return
     */
    @Override
    public Object getProxy(ClassLoader classLoader) {
        return Proxy.newProxyInstance(classLoader,this.advised.getTargetClass().getInterfaces(),this);
    }

    /**
     * 对被代理对象的方法进行增强调用
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        /**
         * 责任链模式：进行封装所有被拦截的方法
         */
        List<Object> interceptersAndDynamicMethodMatchers =  this.advised.getInterceptorsAndDynamicInerceptionAdvice(method,this.advised.getTargetClass());

        GPMethodInvocation invocation = new GPMethodInvocation(
                proxy,this.advised.getTarget(),method,args,this.advised.getTargetClass(),interceptersAndDynamicMethodMatchers);

        /**
         * 调用方法
         */
        return invocation.proceed();
    }
}
