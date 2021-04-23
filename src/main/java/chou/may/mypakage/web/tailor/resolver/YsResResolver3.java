package chou.may.mypakage.web.tailor.resolver;

import chou.may.mypakage.web.tailor.DataTypeEnum;
import chou.may.mypakage.web.tailor.exception.CallFailedException;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * 有数接口响应检查器（若不符合该情况则另外加）
 * @author lin.xc
 * @date 2021/4/16
 **/
@Slf4j
public class YsResResolver3 extends AbsTailorApiResResolver {

    @Override
    public String getName() {
        return "有数接口响应检查器3";
    }

    @Override
    public String getNote() {
        return "欢迎补充！针对接口[教育部高校学历姓名及证件号码查询接口]";
    }

    @Override
    public DataTypeEnum getResDataType() {
        return DataTypeEnum.XML;
    }

    @Override
    public String checkAndGetEffectiveResDataStr(String responseStr) {
        JSONObject resJson = JSONObject.parseObject(responseStr);
        String code = resJson.getString("code");
        // 接口调用失败
        if(!"00".equals(code)){
            throw new CallFailedException(responseStr);
        }
        // 将有效的响应返回
        return resJson.getString("datas");
    }

    @Override
    public List<Map<String, Object>> doResolveResForOutColNameAndValue(String resDataStr){
        // 数组类型返回
        return this.outParamResolver.getColNameWithOutParamValue(resDataStr);
    }

}
