package chou.may.wechat.custom.sal.request;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 组织机构代码证
 * @author Lin.xc
 * @date 2020/2/14
 * */
@Data
public class ApplymentOrganizationInfo {
    /**
     * 组织机构代码证照片
     * */
    @JSONField(name = "organization_copy")
    private String organizationCopy;
    /**
     * 组织机构代码
     * */
    @JSONField(name = "organization_code")
    private String organizationCode;
    /**
     * 组织机构代码证有效期开始日期
     * */
    @JSONField(name = "org_period_begin")
    private String orgPeriodBegin;
    /**
     * 组织机构代码证有效期结束日期
     * */
    @JSONField(name = "org_period_end")
    private String orgPeriodEnd;
}
