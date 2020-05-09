package chou.may.wechat.custom.sal.request;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * APP场景
 * @author Lin.xc
 * @date 2020/2/14
 * */
@Data
public class ApplymentAppInfo {

    /**
     * 服务商应用APPID
     * */
    @JSONField(name = "app_appid")
    private String appAppid;
    /**
     * 商家应用APPID
     * */
    @JSONField(name = "app_sub_appid")
    private String appSubAppid;
    /**
     * APP截图
     * */
    @JSONField(name = "app_pics")
    private List<String> appPics;


}
