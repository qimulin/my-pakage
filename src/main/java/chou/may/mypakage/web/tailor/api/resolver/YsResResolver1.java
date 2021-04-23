package chou.may.mypakage.web.tailor.api.resolver;

import chou.may.mypakage.web.tailor.api.DataTypeEnum;
import chou.may.mypakage.web.tailor.api.exception.CallFailedException;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

/**
 * 有数接口响应检查器（若不符合该情况则另外加）
 * @author lin.xc
 * @date 2021/4/14
 **/
@Slf4j
public class YsResResolver1 extends AbsTailorApiResResolver {

    @Override
    public String getName() {
        return "有数接口响应检查器1";
    }

    @Override
    public String getNote() {
        return "欢迎补充！针对接口[市税务局企业缴税银行账号查询宁波市税务局受限]";
    }

    @Override
    public DataTypeEnum getResDataType() {
        return DataTypeEnum.JSON;
    }

    @Override
    public String checkAndGetEffectiveResDataStr(String responseStr) {
        JSONObject resJson = JSONObject.parseObject(responseStr);
        String code = resJson.getString("code");
        if(!"00".equals(code)){
            throw new CallFailedException(responseStr);
        }
        JSONObject datas = resJson.getJSONObject("datas");
        String datasCode = datas.getString("code");
        if(datasCode!=null){
            // 正常情况下，不用返回提示码，所以这里就是没查到或者什么问题，报个警告
            log.warn("解析响应，接口返回数据无法转换，datas={}", JSONObject.toJSONString(datas));
            // 返回空对象
            return null;
        }
        // 将有效的响应返回
        return JSONObject.toJSONString(datas);
    }

}
