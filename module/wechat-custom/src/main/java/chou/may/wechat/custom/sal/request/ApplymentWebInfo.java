package chou.may.wechat.custom.sal.request;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 互联网网站场景
 * @author Lin.xc
 * @date 2020/2/14
 * */
@Data
public class ApplymentWebInfo {

    /**
     * 互联网网站域名
     * */
    @JSONField(name = "domain")
    private String domain;
    /**
     * 网站授权函
     * */
    @JSONField(name = "web_authorisation")
    private String webAuthorisation;
    /**
     * 互联网网站对应的商家APPID
     * */
    @JSONField(name = "web_appid")
    private String webAppid;


}
