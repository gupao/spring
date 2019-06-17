package spring.framework.beans.support;

import spring.framework.beans.config.GPBeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * created by xuyahui on 2019/4/10
 */
public class GPBeanDefinitionReader {

    private Properties config = new Properties();

    private List<String> registryBeanClasses = new ArrayList<>();

    // 固定配置文件中的key，相当于xml规范
    private final String SCAN_PACKAGE = "scanPackage";

    public GPBeanDefinitionReader(String... locations){
        // 通过url定位找到所对应的文件，转换为文件流
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(locations[0].replace("classpath:",""));
        try {
            config.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(null != in){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        doScan(config.getProperty(SCAN_PACKAGE));
    }

    /**
     * 扫描路径下的文件
     * @param scanPackage
     */
    private void doScan(String scanPackage) {
//        URL url = this.getClass().getClassLoader().getResource("/"+scanPackage.replaceAll("\\.","/"));
        URL url = this.getClass().getResource("/"+scanPackage.replaceAll("\\.","/"));
        File files = new File(url.getFile());
        for (File file : files.listFiles()) {
            if(file.isDirectory()){
                doScan(scanPackage + "." + file.getName());
            }else {
                if(!file.getName().endsWith(".class")){continue;}
                String className = scanPackage + "." + file.getName().replace(".class","");
                registryBeanClasses.add(className);
            }
        }
    }

    public Properties getConfig(){
        return this.config;
    }


    /**
     * 加载完之后，直接返回bean定义的集合，简化设计
     * 把配置文件中扫描到的所有配置信息转换为Beandefinition对象，以便于之后IOC操作方便
     * @return
     */
//    public List<GPBeanDefinition> loadBeanDefinitions(){
//        List<GPBeanDefinition> beanDefinitionList = new ArrayList<>();
//        for (String className : registryBeanClasses) {
//            GPBeanDefinition beanDefinition = doCreateBeanDefinition(className);
//            if(null == beanDefinition){ continue;}
//            beanDefinitionList.add(beanDefinition);
//        }
//        return beanDefinitionList;
//    }
//
//    private GPBeanDefinition doCreateBeanDefinition(String className) {
//        try {
//            Class<?> beanClass = Class.forName(className);
//            if(beanClass.isInterface()){return null;}
//            GPBeanDefinition beanDefinition = new GPBeanDefinition();
//            beanDefinition.setBeanClassName(className);
//            beanDefinition.setFactoryBeanName(toLowerFirstCase(beanClass.getSimpleName()));
////            beanDefinition.setFactoryBeanName(beanClass.getSimpleName());
//            return beanDefinition;
//
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    //把配置文件中扫描到的所有的配置信息转换为GPBeanDefinition对象，以便于之后IOC操作方便
    public List<GPBeanDefinition> loadBeanDefinitions(){
        List<GPBeanDefinition> result = new ArrayList<GPBeanDefinition>();
        try {
            for (String className : registryBeanClasses) {
                Class<?> beanClass = Class.forName(className);
                //如果是一个接口，是不能实例化的
                //用它实现类来实例化
                if(beanClass.isInterface()) { continue; }

                //beanName有三种情况:
                //1、默认是类名首字母小写
                //2、自定义名字
                //3、接口注入
                result.add(doCreateBeanDefinition(toLowerFirstCase(beanClass.getSimpleName()),beanClass.getName()));
//                result.add(doCreateBeanDefinition(beanClass.getName(),beanClass.getName()));

                Class<?> [] interfaces = beanClass.getInterfaces();
                for (Class<?> i : interfaces) {
                    //如果是多个实现类，只能覆盖
                    //为什么？因为Spring没那么智能，就是这么傻
                    //这个时候，可以自定义名字
                    result.add(doCreateBeanDefinition(i.getName(),beanClass.getName()));
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }


    //把每一个配信息解析成一个BeanDefinition
    private GPBeanDefinition doCreateBeanDefinition(String factoryBeanName,String beanClassName){
        GPBeanDefinition beanDefinition = new GPBeanDefinition();
        beanDefinition.setBeanClassName(beanClassName);
        beanDefinition.setFactoryBeanName(factoryBeanName);
        return beanDefinition;
    }

    /**
     * 类名首字母小写转换
     *
     * @param simpleName
     * @return
     */
    private String toLowerFirstCase(String simpleName){
        char[] chars = simpleName.toCharArray();
        /**
         * 之所以加，是因为大小写字母的ASCII 相差32，
         * 且大写字母的ASCII小于小写字母的ASCII，
         * java中，对char做算术运算，实际上就是对ASCII做算术运算
         */
        chars[0] += 32;
        return String.valueOf(chars);
    }


}
