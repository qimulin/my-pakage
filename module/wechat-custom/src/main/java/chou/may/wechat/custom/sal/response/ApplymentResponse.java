package chou.may.wechat.custom.sal.response;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 提交申请单 响应参数
 * @author Lin.xc
 * @date 2020/2/14
 */
@Data
public class ApplymentResponse {
    /**
     * 微信支付申请单号
     * */
    @JSONField(name = "applyment_id")
    private String applymentId;
}
