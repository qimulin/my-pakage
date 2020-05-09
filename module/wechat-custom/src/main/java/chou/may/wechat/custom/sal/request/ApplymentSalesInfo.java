package chou.may.wechat.custom.sal.request;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * 经营场景
 * @author Lin.xc
 * @date 2020/2/14
 * */
@Data
public class ApplymentSalesInfo {
    /**
     * 经营场景类型
     * */
    @JSONField(name = "sales_scenes_type")
    private List<String> salesScenesType;
    /**
     * 线下门店场景
     * */
    @JSONField(name = "biz_store_info")
    private ApplymentBizStoreInfo bizStoreInfo;
    /**
     * 公众号场景
     * */
    @JSONField(name = "mp_info")
    private ApplymentMpInfo mpInfo;
    /**
     * 小程序场景
     * */
    @JSONField(name = "mini_program_info")
    private ApplymentMiniProgramInfo miniProgramInfo;
    /**
     * APP场景
     * */
    @JSONField(name = "app_info")
    private ApplymentAppInfo appInfo;
    /**
     * 互联网网站场景
     * */
    @JSONField(name = "web_info")
    private ApplymentWebInfo webInfo;
    /**
     * 企业微信场景
     * */
    @JSONField(name = "wework_info")
    private ApplymentWeworkInfo weworkInfo;
}
