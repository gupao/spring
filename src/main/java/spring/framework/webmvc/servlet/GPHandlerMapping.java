package spring.framework.webmvc.servlet;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * created by xuyahui on 2019/4/25
 */
public class GPHandlerMapping {

    private Object controller;// 方法对应的实例
    private Method method;// 映射的方法
    private Pattern pattern;// url 的正则匹配

    public GPHandlerMapping(Pattern pattern,Object controller, Method method) {
        this.pattern = pattern;
        this.controller = controller;
        this.method = method;
    }

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }
}
