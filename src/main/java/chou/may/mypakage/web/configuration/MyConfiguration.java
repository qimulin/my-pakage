package chou.may.mypakage.web.configuration;

import chou.may.mypakage.web.annotation.EnumFieldWebReturn;
import chou.may.mypakage.web.annotation.EnumWebReturn;
import chou.may.mypakage.web.exception.ReturnNameDuplicateException;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author lin.xc
 * @date 2021/1/13
 **/
@Configuration
public class MyConfiguration {

    private static Logger log = LoggerFactory.getLogger(MyConfiguration.class);

    @Bean("webReturnEnumMap")
    public Map<String, List<Map<String, Object>>> buildWebReturnEnumMap() {
        // 定义返回
        Map<String, List<Map<String, Object>>> enumConstantListMap = new HashMap<>();
        String packageName = "chou.may.mypakage";
        Reflections reflections = new Reflections(packageName);
        // 查找该包下被自定义注解修饰的类
        Set<Class<?>> classesList = reflections.getTypesAnnotatedWith(EnumWebReturn.class);
        // 如果没有，则直接返回
        if(CollectionUtils.isEmpty(classesList)){
            return enumConstantListMap;
        }
        try {
            for (Class clz : classesList) {
                if (clz.isEnum()) {
                    // 用以记录@EnumFieldWebReturn修饰的字段信息
                    Map<String, Field> returnEnumFieldMap = new HashMap<>(3);
                    // 获取字段
                    Field[] enumFields = clz.getDeclaredFields();
                    // 记录被@EnumFieldWebReturn修饰的常量
                    for(Field enumField: enumFields){
                        if(clz.equals(enumField.getType())){
                            // 不取枚举对象字段
                            continue;
                        }

                        if(enumField.isAnnotationPresent(EnumFieldWebReturn.class)){
                            EnumFieldWebReturn fieldAnnotation = enumField.getAnnotation(EnumFieldWebReturn.class);
                            String key = fieldAnnotation.value();
                            if(StringUtils.isEmpty(key)){
                                key = enumField.getName();
                            }
                            returnEnumFieldMap.put(key, enumField);
                        }
                    }

                    // 没有指定枚举返回字段，则不处理该枚举
                    if(CollectionUtils.isEmpty(returnEnumFieldMap)){
                        log.warn("指定Web返回的枚举类{}没有指定返回字段，跳过处理", clz.getName());
                        continue;
                    }

                    // 获取@EnumWebReturn指定的返回
                    EnumWebReturn enumAnnotation = (EnumWebReturn)clz.getAnnotation(EnumWebReturn.class);
                    String enumKey = enumAnnotation.value();
                    if(StringUtils.isEmpty(enumKey)){
                        enumKey = clz.getSimpleName();
                    }

                    List<Map<String, Object>> enumConstantList= new ArrayList<>(3);

                    // 获取每个枚举常量对象
                    Object[] enumConstants = clz.getEnumConstants();
                    // 遍历每个枚举常量
                    for(Object enumConstant: enumConstants){
                        Map<String, Object> enumFieldMap = new HashMap<>(3);
                        // 将需要设置的字段值都设置到返回结果中
                        for(Map.Entry<String, Field> e: returnEnumFieldMap.entrySet()){
                            // 获取字段
                            Field f = e.getValue();
                            // 字段设置可访问
                            f.setAccessible(true);
                            // 获取字段值
                            Object fValue =f.get(enumConstant);
                            // 设置指定名称和值
                            enumFieldMap.put(e.getKey(), fValue);
                        }
                        enumConstantList.add(enumFieldMap);
                    }
                    // 重名判断
                    if(enumConstantListMap.containsKey(enumKey)){
                        throw new ReturnNameDuplicateException("指定Web返回的枚举类存在命名重名，重名字符串为："+enumKey);
                    }
                    enumConstantListMap.put(enumKey, enumConstantList);
                    log.debug("指定Web返回的枚举类{}放入Web返回的集合", clz.getName());
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return enumConstantListMap;
    }

}
