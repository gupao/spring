package spring.framework.aop.aspect;

import spring.framework.aop.intercept.GPMethodInterceptor;
import spring.framework.aop.intercept.GPMethodInvocation;

import java.lang.reflect.Method;

/**
 * created by xuyahui on 2019/5/26
 */
public class GPAfterReturningAdviceInterceptor extends GPAspectAdviceAbstract implements GPAdvice,GPMethodInterceptor {

    private GPJoinPoint joinPoint;

    public GPAfterReturningAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    @Override
    public Object invoke(GPMethodInvocation mi) throws Exception {
        Object resultValue = mi.proceed();
        this.joinPoint = mi;
        this.afterReturning(resultValue,mi.getMethod(),mi.getArguments(),mi.getThis());
        return resultValue;
    }

    public void afterReturning(Object resultValue, Method method, Object[] arguments, Object aThis) throws Exception{
        super.invokeAdviceMethod(joinPoint,resultValue,null);
    }
}
