package spring.framework.aop.aspect;

import spring.framework.aop.intercept.GPMethodInterceptor;
import spring.framework.aop.intercept.GPMethodInvocation;

import java.lang.reflect.Method;

/**
 * created by xuyahui on 2019/5/26
 */
public class GPAfterThrowingAdviceInterceptor extends GPAspectAdviceAbstract implements GPAdvice,GPMethodInterceptor {

    private String throwName;

    public GPAfterThrowingAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    @Override
    public Object invoke(GPMethodInvocation invocation) throws Exception {
        try {
            return invocation.proceed();
        } catch (Throwable e) {
            invokeAdviceMethod(invocation,null,e.getCause());
            throw e;
        }
    }

    public void setThrowName(String throwName){
        this.throwName = throwName;
    }
}
