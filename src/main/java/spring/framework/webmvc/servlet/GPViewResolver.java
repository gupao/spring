package spring.framework.webmvc.servlet;

import com.sun.deploy.util.StringUtils;

import java.io.File;
import java.util.Locale;

/**
 * created by xuyahui on 2019/5/13
 */
public class GPViewResolver {

    private File templateDir;

    private static final String DEFAULT_TEMPLATE_SUFFX = ".html";


    public GPViewResolver(String templateRoot) {
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        this.templateDir = new File(templateRootPath);
    }

    public GPView resolveViewName(String viewName, Locale locale) throws Exception{
        if(null == viewName || "".equals(viewName)){ return null;}
        viewName = viewName.endsWith(DEFAULT_TEMPLATE_SUFFX) ? viewName : (viewName + DEFAULT_TEMPLATE_SUFFX);
        File template = new File((templateDir.getPath() + "/" + viewName).replaceAll("/+","/"));
        return new GPView(template);
    }
}
