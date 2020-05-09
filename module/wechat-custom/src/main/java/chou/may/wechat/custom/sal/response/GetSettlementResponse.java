package chou.may.wechat.custom.sal.response;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 查询结算账户 响应
 * @author Lin.xc
 * @date 2020/2/17
 */
@Data
public class GetSettlementResponse {
    /**
     * 账户类型
     * */
    @JSONField(name  = "account_type")
    private String accountType;
    /**
     * 开户银行
     * */
    @JSONField(name  = "account_bank")
    private String accountBank;
    /**
     * 开户银行全称（含支行)
     * */
    @JSONField(name  = "bank_name")
    private String bankName;
    /**
     * 开户银行联行号
     * */
    @JSONField(name  = "bank_branch_id")
    private String bankBranchId;
    /**
     * 银行账号
     * */
    @JSONField(name  = "account_number")
    private String accountNumber;
    /**
     * 汇款验证结果
     * */
    @JSONField(name  = "verify_result")
    private String verifyResult;
}
