package chou.may.mypakage.web.tailor.resolver;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

/**
 * 有数接口的响应解析1（若不符合该情况则另外加）
 * @author lin.xc
 * @date 2021/3/31
 **/
@Slf4j
public class YsResResolver1 implements ResResolver {

    @Override
    public String getName() {
        return "有数接口响应解析器1";
    }

    @Override
    public String getNote() {
        return "外层有code和msg等信息，内层也有code，需要对应判断";
    }

    @Override
    public JSONObject resolveResDataToJson(String resultStr) {
        JSONObject resJson = JSONObject.parseObject(resultStr);
        String code = resJson.getString("code");
        if(!"00".equals(code)){
            throw new RuntimeException("解析响应，接口调用失败！响应结果="+resultStr);
        }
        JSONObject datas = resJson.getJSONObject("datas");
        String datasCode = datas.getString("code");
        if(datasCode!=null){
            // 正常情况下，不用返回提示码，所以这里就是没查到或者什么问题，报个警告
            log.warn("解析响应，接口返回数据无法转换，datas={}", JSONObject.toJSONString(datas));
            // 返回空对象
            return null;
        }
        return datas;
    }

}
