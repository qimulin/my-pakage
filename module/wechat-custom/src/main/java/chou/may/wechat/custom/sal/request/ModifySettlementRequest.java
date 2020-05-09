package chou.may.wechat.custom.sal.request;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * 修改结算帐号 请求
 * @author Lin.xc
 * @date 2020/2/17
 */
@Data
public class ModifySettlementRequest extends BaseRequest{
    /**
     * 账户类型
     * */
    @JSONField(name = "account_type")
    private List<String> accountType;
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
     * 开户银行全称（含支行）
     * */
    @JSONField(name = "bank_name")
    private String bankName;
    /**
     * 开户银行联行号
     * */
    @JSONField(name = "bank_branch_id")
    private String bankBranchId;
    /**
     * 银行账号
     * */
    @JSONField(name = "account_number")
    private String accountNumber;
}
