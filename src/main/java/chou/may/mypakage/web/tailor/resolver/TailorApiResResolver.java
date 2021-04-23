package chou.may.mypakage.web.tailor.resolver;

import chou.may.mypakage.web.tailor.DataTypeEnum;

import java.util.List;
import java.util.Map;

/**
 * 定制接口响应解析器
 * @author lin.xc
 * @date 2021/4/14
 **/
public interface TailorApiResResolver {

    /**
     * 名称
     * */
    String getName();

    /**
     * 描述
     * */
    default String getNote(){
        return "无";
    }

    /**
     * 获取返回数据类型
     * */
    DataTypeEnum getResDataType();

    /**
     * 检查并返回有效的响应数据
     * 【说明】：主要为检查响应结果是否报错，报错则抛出。若查不到数据，则返回空即可
     * @param responseStr 响应字符串
     * */
    String checkAndGetEffectiveResDataStr(String responseStr);

    /**
     * 获取列名和输出参数值关系（注意：之所以用List是因为处理入参和出参一对多的时候）
     * @param resDataStr 数据字符串（注意：此字符串需要已经是合法数据）
     */
    List<Map<String, Object>> getColNameWithOutParamValue(String resDataStr);

}
