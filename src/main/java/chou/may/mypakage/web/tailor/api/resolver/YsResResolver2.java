package chou.may.mypakage.web.tailor.api.resolver;

import chou.may.mypakage.web.tailor.api.DataTypeEnum;
import chou.may.mypakage.web.tailor.api.exception.CallFailedException;
import chou.may.mypakage.web.tailor.api.exception.ReturnDataErrorException;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * 有数接口响应检查器（若不符合该情况则另外加）
 * @author lin.xc
 * @date 2021/4/14
 **/
@Slf4j
public class YsResResolver2 extends AbsTailorApiResResolver {

    @Override
    public String getName() {
        return "有数接口响应检查器2";
    }

    @Override
    public String getNote() {
        return "欢迎补充！针对接口[市房管商品房交易备案信息查询接口]";
    }

    @Override
    public DataTypeEnum getResDataType() {
        return DataTypeEnum.JSON;
    }

    @Override
    public String checkAndGetEffectiveResDataStr(String responseStr) {
        JSONObject resJson = JSONObject.parseObject(responseStr);
        String code = resJson.getString("code");
        // 接口调用失败
        if(!"00".equals(code)){
            throw new CallFailedException(responseStr);
        }
        JSONObject datas = resJson.getJSONObject("datas");
        // 处理status判断
        String datasStatus = datas.getString("status");
        // 接口无正常返回
        if(!"0".equals(datasStatus)){
            throw new ReturnDataErrorException(responseStr);
        }
        // 正常返回
        JSONObject datasResult = datas.getJSONObject("result");
        // 将有效的响应返回
        return datasResult.getString("datas");
    }

    @Override
    public List<Map<String, Object>> doResolveResForOutColNameAndValue(String resDataStr){
        // 数组类型返回
        return this.outParamResolver.getColNameWithOutParamValueForArr(resDataStr);
    }

}
