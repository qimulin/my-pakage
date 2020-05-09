package chou.may.wechat.custom.sal.request;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 结算银行账户
 * @author Lin.xc
 * @date 2020/2/14
 * */
@Data
public class ApplymentBankAccountInfo {

    /**
     * 账户类型
     * */
    @JSONField(name = "bank_account_type")
    private String bankAccountType;
    /**
     * 开户名称
     * */
    @JSONField(name = "account_name")
    private String accountName;
    /**
     * 开户银行
     * */
    @JSONField(name = "account_bank")
    private String accountBank;
    /**
     * 开户银行省市编码
     * */
    @JSONField(name = "bank_address_code")
    private String bankAddressCode;
    /**
     * 开户银行联行号
     * */
    @JSONField(name = "bank_branch_id")
    private String bankBranchId;
    /**
     * 开户银行全称（含支行)
     * */
    @JSONField(name = "bank_name")
    private String bankName;
    /**
     * 银行账号
     * */
    @JSONField(name = "account_number")
    private String accountNumber;
}
