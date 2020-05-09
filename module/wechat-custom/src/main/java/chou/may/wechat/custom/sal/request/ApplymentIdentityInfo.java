package chou.may.wechat.custom.sal.request;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 经营者/法人身份证件
 * @author Lin.xc
 * @date 2020/2/14
 * */
@Data
public class ApplymentIdentityInfo {

    /**
     * 证件类型
     * */
    @JSONField(name = "id_doc_type")
    private String idDocType;
    /**
     * 身份证信息
     * */
    @JSONField(name = "id_card_info")
    private ApplymentIdCardInfo idCardInfo;
    /**
     * 其他类型证件信息
     * */
    @JSONField(name = "id_doc_info")
    private ApplymentIdDocInfo idDocInfo;
    /**
     * 经营者/法人是否为受益人
     * */
    @JSONField(name = "owner")
    private Boolean owner = true;

}
