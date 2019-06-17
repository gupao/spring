package spring.framework.aop.aspect;

import java.lang.reflect.Method;

/**
 * created by xuyahui on 2019/5/26
 */
public abstract class  GPAspectAdviceAbstract implements GPAdvice {

    private Method aspectMethod;

    private Object aspectTarget;


    public GPAspectAdviceAbstract(Method aspectMethod,Object aspectTarget) {
        this.aspectMethod = aspectMethod;
        this.aspectTarget = aspectTarget;
    }

    public Object invokeAdviceMethod(GPJoinPoint joinPoint, Object resultValue, Throwable tx) throws Exception{
        Class<?> [] paramTypes = this.aspectMethod.getParameterTypes();
        if(null == paramTypes || paramTypes.length == 0){
            return this.aspectMethod.invoke(aspectTarget);
        }else {
            Object[] args = new Object[paramTypes.length];
            for(int i=0;i<paramTypes.length;i++){
                if(paramTypes[i] == GPJoinPoint.class){
                    args[i] = joinPoint;
                }else if(paramTypes[i] == Throwable.class){
                    args[i] = tx;
                }else if(paramTypes[i] == Object.class){
                    args[i] = resultValue;
                }
            }
            return this.aspectMethod.invoke(aspectTarget,args);
        }
    }
}
