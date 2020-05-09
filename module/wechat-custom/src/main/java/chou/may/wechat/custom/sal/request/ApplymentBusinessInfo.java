package chou.may.wechat.custom.sal.request;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 经营资料
 * @author Lin.xc
 * @date 2020/2/14
 * */
@Data
public class ApplymentBusinessInfo {
    /**
     * 商户简称
     * */
    @JSONField(name = "merchant_shortname")
    private String merchantShortname;
    /**
     * 客服电话
     * */
    @JSONField(name = "service_phone")
    private String servicePhone;
    /**
     * 经营场景
     * */
    @JSONField(name = "sales_info")
    private ApplymentSalesInfo salesInfo;
}
