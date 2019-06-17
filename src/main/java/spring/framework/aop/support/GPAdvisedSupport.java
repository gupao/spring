package spring.framework.aop.support;

import com.sun.corba.se.impl.ior.OldJIDLObjectKeyTemplate;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import spring.framework.aop.aspect.GPAfterReturningAdviceInterceptor;
import spring.framework.aop.aspect.GPAfterThrowingAdviceInterceptor;
import spring.framework.aop.aspect.GPMethodBeforeAdviceInterceptor;
import spring.framework.aop.config.GPAopConfig;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * created by xuyahui on 2019/5/19
 */
@Slf4j
public class GPAdvisedSupport {

    private Class<?> targetClass;

    private Object target;

    private Pattern pointCutClassPattern;

    private GPAopConfig config;

    private transient Map<Method,List<Object>> methodCache;

    public GPAdvisedSupport(GPAopConfig config) {
        this.config = config;
    }

    public Class<?> getTargetClass(){
        return this.targetClass;
    }

    public Object getTarget(){
        return this.target;
    }

    public List<Object> getInterceptorsAndDynamicInerceptionAdvice(Method method,Class<?> targetClass) throws NoSuchMethodException {
        List<Object> cached = methodCache.get(method);
        if(null == cached){
            Method m = targetClass.getMethod(method.getName(),method.getParameterTypes());
            cached = methodCache.get(m);
            //底层逻辑，对代理方法进行一个兼容处理
            methodCache.put(m,cached);
        }
        return cached;
    }

    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
        parse();
    }

    /**
     * 匹配满足切面规则的方法
     */
    private void parse() {
        // pointCut=public .* com.gupaoedu.vip.spring.demo.service..*Service..*(.*)
//        String pointCut = config.getPointCut()
//                .replaceAll("\\.","\\\\.")// 将 【.】 替换为 【\.】
//                .replaceAll("\\\\.\\*",".*")// 将【\.*】 替换为 【.*】
//                .replaceAll("\\(","\\\\(")// 将【(】 替换为 【\(】
//                .replaceAll("\\)","\\\\)");// 将【\)】 替换为 【\)】
//        String pointCutForClassRegex = pointCut.substring(0,pointCut.lastIndexOf("\\(")-4);
//        pointCutClassPattern = Pattern.compile("class " + pointCutForClassRegex.substring(
//                pointCutForClassRegex.lastIndexOf(" " +1)));
//
//        log.info(config.getPointCut());
//        log.info(pointCut);
//        log.info(pointCutForClassRegex);

        String pointCut = config.getPointCut()
                .replaceAll("\\.","\\\\.")
                .replaceAll("\\\\.\\*",".*")
                .replaceAll("\\(","\\\\(")
                .replaceAll("\\)","\\\\)");
        //pointCut=public .* com.gupaoedu.vip.spring.demo.service..*Service..*(.*)
        //玩正则
        String pointCutForClassRegex = pointCut.substring(0,pointCut.lastIndexOf("\\(") - 4);
        pointCutClassPattern = Pattern.compile("class " + pointCutForClassRegex.substring(
                pointCutForClassRegex.lastIndexOf(" ") + 1));


        try {
            methodCache = new HashMap<Method, List<Object>>();
            Pattern pattern = Pattern.compile(pointCut);

            Class aspectClazz = Class.forName(this.config.getAspectClass());
            Map<String,Method> aspectMethods = new HashMap<>();
            for (Method m : aspectClazz.getMethods()) {
                aspectMethods.put(m.getName(),m);
            }


            for (Method method : this.targetClass.getMethods()) {
                String methodStr = method.toString();
                if(methodStr.contains("throws")){
                    methodStr = methodStr.substring(0,methodStr.lastIndexOf("throws")).trim();
                }

                Matcher matcher = pattern.matcher(methodStr);
                if(matcher.matches()){
                    // 如果匹配成功，把每个方法包装成 methodInterceptor

                    List<Object> advices = new LinkedList<>();// 执行器链

                    // before
                    if(!(null == config.getAspectBefore() || "".equals(config.getAspectBefore()))){
                        advices.add(new GPMethodBeforeAdviceInterceptor(aspectMethods.get(this.config.getAspectBefore()),aspectClazz.newInstance()));
                    }

                    // after
                    if(!(null == config.getAspectAfter() || "".equals(config.getAspectAfter()))){
                        advices.add(new GPAfterReturningAdviceInterceptor(aspectMethods.get(this.config.getAspectAfter()),aspectClazz.newInstance()));

                    }
                    // afterThrowing
                    if(!(null == config.getAspectAfterThrow() || "".equals(config.getAspectAfterThrow()))){

                        GPAfterThrowingAdviceInterceptor throwingAdvice =
                                new GPAfterThrowingAdviceInterceptor(aspectMethods.get(this.config.getAspectAfterThrow()),aspectClazz.newInstance());
                        throwingAdvice.setThrowName(this.config.getAspectAfterThrowingName());
                        advices.add(throwingAdvice);
                    }
                    methodCache.put(method,advices);
                }
            }


        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public boolean pointCutMatch() {
        System.out.println(this.targetClass.toString());
        return this.pointCutClassPattern.matcher(this.targetClass.toString()).matches();
    }
}
