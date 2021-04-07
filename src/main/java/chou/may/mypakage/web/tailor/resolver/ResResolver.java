package chou.may.mypakage.web.tailor.resolver;

import com.alibaba.fastjson.JSONObject;

/**
 * @author lin.xc
 * @date 2021/3/31
 **/
public interface ResResolver {

    /**
     * 描述
     * */
    String getName();

    /**
     * 描述
     * */
    default String getNote(){
        return "无";
    }

    /**
     * 解析响应给请求
     * */
    JSONObject resolveResDataToJson(String resultStr);
    /**
     * 解析响应给请求
     * */
    default String resolveResDataToStr(String resultStr){
        return JSONObject.toJSONString(resolveResDataToJson(resultStr));
    }
}
