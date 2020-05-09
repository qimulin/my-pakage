package chou.may.wechat.custom.sal.request;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * 企业微信场景
 * @author Lin.xc
 * @date 2020/2/14
 * */
@Data
public class ApplymentWeworkInfo {

    /**
     * 商家企业微信CorpID
     * */
    @JSONField(name = "sub_corp_id")
    private String subCorpId;
    /**
     * 企业微信页面截图
     * */
    @JSONField(name = "wework_pics")
    private List<String> weworkPics;

}
