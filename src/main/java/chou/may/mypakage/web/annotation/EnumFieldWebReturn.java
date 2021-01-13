package chou.may.mypakage.web.annotation;

import java.lang.annotation.*;

/**
 * 枚举字段Web层返回
 * @author lin.xc
 * @date 2021/1/12
 **/
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnumFieldWebReturn {
    String value() default "";
}
