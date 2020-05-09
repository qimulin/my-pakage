package chou.may.wechat.custom.sal.response;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 驳回原因详情
 * @author Lin.xc
 * @date 2020/2/17
 */
@Data
public class GetApplymentAuditDetail {
    /**
     * 字段名
     * */
    @JSONField(name  = "field")
    private String field;
    /**
     * 字段名称
     * */
    @JSONField(name  = "field_name")
    private String fieldName;
    /**
     * 驳回原因
     * */
    @JSONField(name  = "reject_reason")
    private String rejectReason;
}
