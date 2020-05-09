package chou.may.wechat.custom.sal.request;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 提交申请单 请求参数
 * @author Lin.xc
 * @date 2020/2/14
 * */
@Data
public class ApplymentRequest extends BaseRequest{

    /**
     * 业务申请编号
     * */
    @JSONField(name = "business_code")
    private String businessCode;
    /**
     * 超级管理员信息
     * */
    @JSONField(name = "contact_info")
    private ApplymentContactInfo contactInfo;
    /**
     * 主体资料
     * */
    @JSONField(name = "subject_info")
    private ApplymentSubjetInfo subjectInfo;
    /**
     * 经营资料
     * */
    @JSONField(name = "business_info")
    private ApplymentBusinessInfo businessInfo;
    /**
     * 结算规则
     * */
    @JSONField(name = "settlement_info")
    private ApplymentSettlementInfo settlementInfo;
    /**
     * 结算银行账户
     * */
    @JSONField(name = "bank_account_info")
    private ApplymentBankAccountInfo bankAccountInfo;
    /**
     * 补充材料
     * */
    @JSONField(name = "addition_info")
    private ApplymentAdditionInfo additionInfo;

}
