package spring.framework.annotation;

import java.lang.annotation.*;

/**
 * created by xuyahui on 2019/4/21
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@GPIndexed
public @interface GPComponent {

    String value() default "";
}
