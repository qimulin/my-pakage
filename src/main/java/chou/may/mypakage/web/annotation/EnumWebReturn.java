package chou.may.mypakage.web.annotation;

import java.lang.annotation.*;

/**
 * 枚举Web层返回
 * @author lin.xc
 * @date 2021/1/12
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnumWebReturn {
    String name() default "";
}
