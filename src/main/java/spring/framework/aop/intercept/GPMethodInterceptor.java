package spring.framework.aop.intercept;

/**
 * created by xuyahui on 2019/5/19
 */
public interface GPMethodInterceptor {

    /**
     * 就是执行切入点中织入的方法
     * @param invocation
     * @return
     * @throws Exception
     */
    Object invoke(GPMethodInvocation invocation) throws Exception;

}
