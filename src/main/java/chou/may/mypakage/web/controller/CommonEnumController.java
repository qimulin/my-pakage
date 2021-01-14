package chou.may.mypakage.web.controller;

import chou.may.mypakage.web.annotation.EnumWebReturn;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author lin.xc
 * @date 2021/1/12
 **/
@Slf4j
@RestController
@RequestMapping("/common-enum")
public class CommonEnumController  implements ApplicationListener, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Resource(name="webReturnEnumMap")
    private Map<String, List<Map<String, Object>>> webReturnEnumMap;

    @GetMapping("/get")
    public List<Map<String,Object>> listUserInfo(String code){
        return webReturnEnumMap.get(code);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        Class<? extends Annotation> annotationClass = EnumWebReturn.class;
        Map<String,Object> beanWithAnnotation = applicationContext.getBeansWithAnnotation(annotationClass);
        Set<Map.Entry<String,Object>> entitySet = beanWithAnnotation.entrySet();
        for (Map.Entry<String,Object> entry :entitySet){
            Class<? extends Object> clazz = entry.getValue().getClass();//获取bean对象
            System.out.println("================"+clazz.getName());
            EnumWebReturn enumObj = AnnotationUtils.findAnnotation(clazz,EnumWebReturn.class);
            System.out.println("===================");

        }
    }
}
