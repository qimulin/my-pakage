package chou.may.wechat.custom.sal.request;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 最终受益人信息(UBO)
 * @author Lin.xc
 * @date 2020/2/14
 * */
@Data
public class ApplymentUboInfo {

    /**
     * 证件类型
     * */
    @JSONField(name = "id_type")
    private String idType;
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
     * 证件照片
     * */
    @JSONField(name = "id_doc_copy")
    private String idDocCopy;
    /**
     * 受益人姓名
     * */
    @JSONField(name = "name")
    private String name;
    /**
     * 证件号码
     * */
    @JSONField(name = "id_number")
    private String idNumber;
    /**
     * 证件有效期开始时间
     * */
    @JSONField(name = "id_period_begin")
    private String idPeriodBegin;
    /**
     * 证件有效期结束时间
     * */
    @JSONField(name = "id_period_end")
    private String idPeriodEnd;

}
