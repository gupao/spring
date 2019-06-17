package spring.framework.webmvc.servlet;

import lombok.Data;
import spring.framework.annotation.GPRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

/**
 * created by xuyahui on 2019/5/7
 */
@Data
public class GPHandlerAdapter {

    public boolean supports(Object handler){
        return (handler instanceof GPHandlerMapping);
    }

    GPModelAndView handle(HttpServletRequest request, HttpServletResponse response,Object handler) throws Exception{

        GPHandlerMapping handlerMapping = (GPHandlerMapping)handler;


        // 第一步，把方法的形参列表和request的参数列表所在的顺序进行一一对应
        Map<String,Integer> paramIndexMapping = new HashMap<>();
        /**
         * 提取方法中加了注解的参数，把方法上面是注解拿到，得到一个二维数组
         * （因为一个参数可以有多个注解，而一个方法可以有多个参数）
         */
        Annotation[][] annotations = handlerMapping.getMethod().getParameterAnnotations();
        for(int i=0;i<annotations.length;i++){
            for(Annotation a : annotations[i]){
                if(a instanceof GPRequestParam){
                    String paramName = ((GPRequestParam)a).value();
//                    paramIndexMapping.put(paramName,i);
                    // todo 少了判断
                    if(!"".equals(paramName.trim())){
                        paramIndexMapping.put(paramName, i);
                    }
                }
            }
        }


        // 第二步，提取方法中request 和 response 参数
        Class<?>[] paramsTypes = handlerMapping.getMethod().getParameterTypes();
        for(int i= 0;i<paramsTypes.length;i++ ){
            Class<?> type = paramsTypes[i];
            if(type == HttpServletRequest.class || type == HttpServletResponse.class){
                paramIndexMapping.put(type.getName(),i);
            }

        }

        // 第三步, 获得方法的形参列表
        Map<String,String[]> params = request.getParameterMap();
        Object[] paramValues = new Object[paramsTypes.length];

        for (Map.Entry<String, String[]> param : params.entrySet()) {
            String value = Arrays.toString(param.getValue())
                    .replaceAll("\\[|\\]","")
                    .replaceAll("\\s",",");
            if(!paramIndexMapping.containsKey(param.getKey())){continue;}

            int index = paramIndexMapping.get(param.getKey());
            paramValues[index] = caseStringValue(value,paramsTypes[index]);
        }

        // 处理request和Response
        if(paramIndexMapping.containsKey(HttpServletRequest.class.getName())){
            int reqIndex = paramIndexMapping.get(HttpServletRequest.class.getName());
            paramValues[reqIndex] = request;
        }

        if(paramIndexMapping.containsKey(HttpServletResponse.class.getName())){
            int resIndex = paramIndexMapping.get(HttpServletResponse.class.getName());
            paramValues[resIndex] = response;
        }

        // 第四步，反射调用方法得到结果,判断并返回
        Object result = handlerMapping.getMethod().invoke(handlerMapping.getController(),paramValues);
        if(result == null || result instanceof Void){return null;}

        boolean isModelAndView = handlerMapping.getMethod().getReturnType() == GPModelAndView.class;
        if(isModelAndView){
            return (GPModelAndView)result;
        }
        return null;
    }

    private Object caseStringValue(String value, Class<?> paramsType) {
        if(String.class == paramsType){
            return value;
        }else if(Integer.class == paramsType){
            return Integer.valueOf(value);
        }else if(Double.class == paramsType) {
            return Double.valueOf(value);
        }else {
            if(value != null){
                return value;
            }
            return null;
        }
    }

}
