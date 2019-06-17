package spring.framework.aop.aspect;

import spring.framework.aop.intercept.GPMethodInterceptor;
import spring.framework.aop.intercept.GPMethodInvocation;

import java.lang.reflect.Method;

/**
 * created by xuyahui on 2019/5/26
 */
public class GPMethodBeforeAdviceInterceptor extends GPAspectAdviceAbstract implements GPAdvice,GPMethodInterceptor {

    private GPJoinPoint joinPoint;

    public GPMethodBeforeAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    private void before(Method method,Object[] args,Object target)throws Exception{
        // 传送给织入参数
//        method.invoke(target);
        super.invokeAdviceMethod(this.joinPoint,null,null);
    }

    @Override
    public Object invoke(GPMethodInvocation invocation) throws Exception {
        // 参数从被织入的代码中拿到
        this.joinPoint = invocation;
        before(invocation.getMethod(),invocation.getArguments(),invocation.getThis());
        return invocation.proceed();
    }
}
