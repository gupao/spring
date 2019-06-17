package spring.test;

import spring.demo.action.MyAction;
import spring.framework.context.GPApplicationContext;

import javax.xml.ws.Endpoint;

/**
 * created by xuyahui on 2019/4/22
 */
public class IocTest {

    public static void main(String[] args) {

        GPApplicationContext context = new GPApplicationContext("classpath:application.properties");
        try {
            Object object1 = context.getBean("myAction");
            System.out.println(object1);
            System.out.println(MyAction.class.getSimpleName());

            Object myAction = context.getBean(toLowerFirstCase(MyAction.class.getSimpleName()));
            System.out.println(myAction);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        Endpoint.publish();
    }

    /**
     * 类名首字母小写转换
     *
     * @param simpleName
     * @return
     */
    public static String toLowerFirstCase(String simpleName){
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
