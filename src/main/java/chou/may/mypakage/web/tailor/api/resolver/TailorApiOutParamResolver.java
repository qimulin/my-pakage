package chou.may.mypakage.web.tailor.api.resolver;

import java.util.List;
import java.util.Map;

/**
 * API输出参数处理器
 * 设计模型之策略模式
 * @author lin.xc
 * @date 2021/4/14
 **/
public interface TailorApiOutParamResolver {
    /**
     * 解析API输出字段和参数值
     * @param resDataStr 响应数据字符串
     * */
    List<Map<String, Object>> getColNameWithOutParamValue(String resDataStr);

    /**
     * 获取API输出字段和参数值
     * @param resDataStr 响应数据字符串（数组类型）
     * */
    List<Map<String, Object>> getColNameWithOutParamValueForArr(String resDataStr);
}
