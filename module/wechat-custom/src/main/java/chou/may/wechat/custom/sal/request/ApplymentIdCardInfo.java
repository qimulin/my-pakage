package chou.may.wechat.custom.sal.request;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 身份证信息
 * @author Lin.xc
 * @date 2020/2/14
 * */
@Data
public class ApplymentIdCardInfo {

    /**
     * 身份证人像面照片
     * */
    @JSONField(name = "id_card_copy")
    private String idCardCopy;
    /**
     * 身份证国徽面照片
     * */
    @JSONField(name = "id_card_national")
    private String idCardNational;
    /**
     * 身份证姓名
     * */
    @JSONField(name = "id_card_name")
    private String idCardName;
    /**
     * 身份证号码
     * */
    @JSONField(name = "id_card_number")
    private String idCardNumber;
    /**
     * 身份证有效期开始时间
     * */
    @JSONField(name = "card_period_begin")
    private String cardPeriodBegin;
    /**
     * 身份证有效期结束时间
     * */
    @JSONField(name = "card_period_end")
    private String cardPeriodEnd;

}
