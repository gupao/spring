package spring.framework.annotation;

import java.lang.annotation.*;

/**
 * created by xuyahui on 2019/4/21
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@GPComponent
public @interface GPService {

    String value() default "";
}
