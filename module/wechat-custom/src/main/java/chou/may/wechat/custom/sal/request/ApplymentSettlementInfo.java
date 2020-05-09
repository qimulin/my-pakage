package chou.may.wechat.custom.sal.request;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 结算规则
 * @author Lin.xc
 * @date 2020/2/14
 * */
@Data
public class ApplymentSettlementInfo {
    /**
     * 入驻结算规则ID
     * */
    @JSONField(name = "settlement_id")
    private String settlementId;
    /**
     * 所属行业
     * */
    @JSONField(name = "qualification_type")
    private String qualificationType;
    /**
     * 特殊资质图片
     * */
    @JSONField(name = "qualifications")
    private String qualifications;
    /**
     * 优惠费率活动ID
     * */
    @JSONField(name = "activities_id")
    private String activitiesId;
    /**
     * 优惠费率活动值
     * */
    @JSONField(name = "activities_rate")
    private String activitiesRate;
    /**
     * 优惠费率活动补充材料
     * */
    @JSONField(name = "activities_additions")
    private String activitiesAdditions;
}
