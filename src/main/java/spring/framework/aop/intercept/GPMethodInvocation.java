package spring.framework.aop.intercept;

import spring.framework.aop.aspect.GPJoinPoint;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * created by xuyahui on 2019/5/19
 */
public class GPMethodInvocation implements GPJoinPoint {

    private Object proxy;
    private Object target;
    private Method method;
    private Object[] arguments;
    private Class<?> targetClass;
    private List<Object> interceptersAndDynamicMethodMatchers;

    // 定义一个索引，从负1开始记录当前拦截器执行的位置
    private int currentIntercepterIndex = -1;

    private Map<String, Object> userAttributes;


    public GPMethodInvocation(
            Object proxy, Object target, Method method, Object[] arguments,
            Class<?> targetClass, List<Object> interceptersAndDynamicMethodMatchers) {
        this.proxy = proxy;
        this.target = target;
        this.method = method;
        this.arguments = arguments;
        this.targetClass = targetClass;
        this.interceptersAndDynamicMethodMatchers = interceptersAndDynamicMethodMatchers;
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public Object proceed() throws Exception{
        // 如果interceptor执行完了，就执行joinPoint
        if(this.currentIntercepterIndex == this.interceptersAndDynamicMethodMatchers.size() -1){
            return this.method.invoke(this.target,this.arguments);
        }

        Object interceptorOrInterceptionAdvice =
                this.interceptersAndDynamicMethodMatchers.get(++this.currentIntercepterIndex);

        // 如果要动态匹配joinPoint
        if(interceptorOrInterceptionAdvice instanceof GPMethodInterceptor){
            GPMethodInterceptor mi = (GPMethodInterceptor)interceptorOrInterceptionAdvice;
            return mi.invoke(this);
        }else {
            // 动态匹配失败时，虑过当前的interceptor 调用下一个insterceptor
            return proceed();
        }
    }

    @Override
    public Object getThis() {
        return this.target;
    }

    @Override
    public Object[] getArguments() {
        return this.arguments;
    }

    @Override
    public Method getMethod() {
        return this.method;
    }

    @Override
    public void setUserAttribute(String key, Object value) {
        if (value != null) {
            if (this.userAttributes == null) {
                this.userAttributes = new HashMap<String,Object>();
            }
            this.userAttributes.put(key, value);
        }
        else {
            if (this.userAttributes != null) {
                this.userAttributes.remove(key);
            }
        }
    }

    @Override
    public Object getUserAttribute(String key) {
        return (this.userAttributes != null ? this.userAttributes.get(key) : null);
    }
}
