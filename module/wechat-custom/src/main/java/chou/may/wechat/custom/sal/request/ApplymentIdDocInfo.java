package chou.may.wechat.custom.sal.request;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 其他类型证件信息
 * @author Lin.xc
 * @date 2020/2/14
 * */
@Data
public class ApplymentIdDocInfo {

    /**
     * 证件照片
     * */
    @JSONField(name = "id_doc_copy")
    private String idDocCopy;
    /**
     * 证件姓名
     * */
    @JSONField(name = "id_doc_name")
    private String idDocName;
    /**
     * 证件号码
     * */
    @JSONField(name = "id_doc_number")
    private String idDocNumber;
    /**
     * 证件有效期开始时间
     * */
    @JSONField(name = "doc_period_begin")
    private String docPeriodBegin;
    /**
     * 证件有效期结束时间
     * */
    @JSONField(name = "doc_period_end")
    private String docPeriodEnd;

}
