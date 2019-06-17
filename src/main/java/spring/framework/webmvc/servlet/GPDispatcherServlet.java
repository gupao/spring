package spring.framework.webmvc.servlet;

import com.sun.deploy.net.HttpRequest;
import lombok.extern.slf4j.Slf4j;
import spring.framework.annotation.GPController;
import spring.framework.annotation.GPRequestMapping;
import spring.framework.context.GPApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * created by xuyahui on 2019/4/23
 */
@Slf4j
public class GPDispatcherServlet extends HttpServlet {

    private GPApplicationContext context;

    private final String CONTEXT_CONFIG_LOCATION = "contextConfigLocation";

    private List<GPHandlerMapping> handlerMappings = new ArrayList<GPHandlerMapping>();

    private Map<GPHandlerMapping,GPHandlerAdapter> handlerAdapters =  new HashMap<>();

    private List<GPViewResolver> viewResolvers = new ArrayList<>();



    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        super.doGet(req, resp);
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        super.doPost(req, resp);
        try {
            this.doDispatch(req,resp);
        } catch (Exception e) {
            new GPModelAndView("500");
//            processDispatchResult(req,resp,new GPModelAndView("500"));
            resp.getWriter().write("500 Exception,Details:\r\n"
                    + Arrays.toString(e.getStackTrace())
                    .replaceAll("\\[|\\]","")
                    .replaceAll(",\\s","\r\n"));
            e.printStackTrace();
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception{

        // 第一步，通过从request中拿到url，去匹配一个handlerMapping
        GPHandlerMapping handler = getHandler(req);

        if(handler == null){
            processDispatchResult(req,resp,new GPModelAndView("404"));
            return;
        }

        // 第二步，准备调用前的参数
        GPHandlerAdapter ha = getHandlerAdapter(handler);


        // 第三部，真正的调用方法,返回GPModelAndView（存储了要传递到页面上的值，和模板页面的名称）
        GPModelAndView mv = ha.handle(req,resp,handler);

        // 第四步，处理结果(真正的输出)
        processDispatchResult(req,resp,mv);


    }

    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, GPModelAndView mv) throws Exception {
        // 把给我的MedelAndView变成一个，html、outputStream，json，freemmark，vealcity
        // ContextType来决定
        if(null == mv){ return; }

        if(this.viewResolvers.isEmpty()){ return;}

        for (GPViewResolver viewResolver : this.viewResolvers) {
            GPView view = viewResolver.resolveViewName(mv.getViewName(),null);
            view.render(mv.getModel(),req,resp);
            return;
        }

    }

    private GPHandlerAdapter getHandlerAdapter(GPHandlerMapping handler) {
        if(this.handlerAdapters.isEmpty()){return null;}
        GPHandlerAdapter ha = this.handlerAdapters.get(handler);
        if(ha.supports(handler)){
            return ha;
        }
        return null;
    }

    public GPHandlerMapping getHandler(HttpServletRequest request){
        if(this.handlerMappings.isEmpty()){return null;}

        String url = request.getRequestURI();
        String contextPath = request.getContextPath();
        url = url.replaceAll(contextPath,"").replaceAll("/+","/");

        try {
            for (GPHandlerMapping handlerMapping : this.handlerMappings) {
                Matcher matcher = handlerMapping.getPattern().matcher(url);
                if(!matcher.matches()){
                    continue;
                }
                return handlerMapping;
            }
        } catch (Exception e) {
            throw e;
        }
        return null;
    }


    @Override
    public void init(ServletConfig config) throws ServletException {
//        super.init();

        // 1.初始化ApplicationContext
        context = new GPApplicationContext(config.getInitParameter(CONTEXT_CONFIG_LOCATION));

        // 2.初始化spring mvc 的九大组件
        initStrategies(context);
    }

    //初始化策略
    protected void initStrategies(GPApplicationContext context) {
        //多文件上传的组件
        initMultipartResolver(context);
        //初始化本地语言环境
        initLocaleResolver(context);
        //初始化模板处理器
        initThemeResolver(context);

        //handlerMapping，必须实现
        initHandlerMappings(context);
        //初始化参数适配器，必须实现
        initHandlerAdapters(context);
        //初始化异常拦截器
        initHandlerExceptionResolvers(context);
        //初始化视图预处理器
        initRequestToViewNameTranslator(context);

        //初始化视图转换器，必须实现
        initViewResolvers(context);
        //参数缓存器
        initFlashMapManager(context);
    }

    private void initMultipartResolver(GPApplicationContext context) {
    }

    private void initLocaleResolver(GPApplicationContext context){
    }

    private void initThemeResolver(GPApplicationContext context){}

    private void initHandlerMappings(GPApplicationContext context){
        String[] beanNames =  context.getBeanDefinitionNames();
        try {
            for (String beanName : beanNames) {
                Object controller = context.getBean(beanName);
                Class<?> clazz = controller.getClass();
                if(!clazz.isAnnotationPresent(GPController.class)){
                    continue;
                }

                // 获取类上面的URL配置
                String baseUrl = "";
                if(clazz.isAnnotationPresent(GPRequestMapping.class)){
                    GPRequestMapping requestMapping = clazz.getAnnotation(GPRequestMapping.class);
                    baseUrl = requestMapping.value();
                }
                // 获取method上面的URL配置
                for (Method method : clazz.getMethods()) {
                    if(!method.isAnnotationPresent(GPRequestMapping.class)){
                        continue;
                    }
                    GPRequestMapping requestMapping = method.getAnnotation(GPRequestMapping.class);
                    String regex = ("/"+baseUrl+"/"+requestMapping.value().replaceAll("\\*",".*"))
                            .replaceAll("/+","/");
                    Pattern pattern = Pattern.compile(regex);
                    handlerMappings.add(new GPHandlerMapping(pattern,controller,method));
                    log.info("Mapped " + regex + "," + method);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initHandlerAdapters(GPApplicationContext context){
        // 把reqest请求变成handler，有几个handlerMapping，就有几个HandlerAdapter
        for (GPHandlerMapping handlerMapping : this.handlerMappings) {
            this.handlerAdapters.put(handlerMapping,new GPHandlerAdapter());
        }


    }

    private void initHandlerExceptionResolvers(GPApplicationContext context){}

    private void initRequestToViewNameTranslator(GPApplicationContext context){}


    private void initViewResolvers(GPApplicationContext context){
        // 拿到模板的存放目录
        String templateRoot = context.getConfig().getProperty("templateRoot");
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();

        File templateRootDir = new File(templateRootPath);

        for (File file : templateRootDir.listFiles()) {

            this.viewResolvers.add(new GPViewResolver(templateRoot));

        }

    }

    private void initFlashMapManager(GPApplicationContext context){}

}
