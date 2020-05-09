package chou.may.wechat.custom.sal.request;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * 公众号场景
 * @author Lin.xc
 * @date 2020/2/14
 * */
@Data
public class ApplymentMpInfo {

    /**
     * 服务商公众号APPID
     * */
    @JSONField(name = "mp_appid")
    private String mpAppid;
    /**
     * 商家公众号APPID
     * */
    @JSONField(name = "mp_sub_appid")
    private String mpSubAppid;
    /**
     * 公众号页面截图
     * */
    @JSONField(name = "mp_pics")
    private List<String> mpPics;

}
