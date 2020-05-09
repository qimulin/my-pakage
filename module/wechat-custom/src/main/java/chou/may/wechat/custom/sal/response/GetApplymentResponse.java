package chou.may.wechat.custom.sal.response;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * 查询申请状态 响应
 * @author Lin.xc
 * @date 2020/2/17
 */
@Data
public class GetApplymentResponse {
    /**
     * 业务申请编号
     * */
    @JSONField(name  = "business_code")
    private String businessCode;
    /**
     * 微信支付申请单号
     * */
    @JSONField(name  = "applyment_id")
    private String applymentId;
    /**
     * 特约商户号
     * */
    @JSONField(name  = "sub_mchid")
    private String subMchid;
    /**
     * 超级管理员签约链接
     * */
    @JSONField(name  = "sign_url")
    private String signUrl;
    /**
     * 申请单状态
     * */
    @JSONField(name  = "applyment_state")
    private String applymentState;
    /**
     * 申请状态描述
     * */
    @JSONField(name  = "applyment_state_msg")
    private String applymentStateMsg;
    /**
     * 驳回原因详情
     * */
    @JSONField(name  = "audit_detail")
    private List<GetApplymentAuditDetail> auditDetail;

}
