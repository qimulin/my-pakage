package chou.may.wechat.custom.sal.request;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 主体资料
 * @author Lin.xc
 * @date 2020/2/14
 * */
@Data
public class ApplymentSubjetInfo {
    /**
     * 主体类型
     * */
    @JSONField(name = "subject_type")
    private String subjectType;
    /**
     * 营业执照
     * */
    @JSONField(name = "business_license_info")
    private ApplymentBusinessLicenseInfo businessLicenseInfo;
    /**
     * 登记证书
     * */
    @JSONField(name = "certificate_info")
    private ApplymentCertificateInfo certificateInfo;
    /**
     * 组织机构代码证
     * */
    @JSONField(name = "organization_info")
    private ApplymentOrganizationInfo organizationInfo;
    /**
     * 单位证明函照片
     * */
    @JSONField(name = "certificate_letter_copy")
    private String certificateLetterCopy;
    /**
     * 经营者/法人身份证件
     * */
    @JSONField(name = "identity_info")
    private ApplymentIdentityInfo identityInfo;
    /**
     * 最终受益人信息(UBO)
     * */
    @JSONField(name = "ubo_info")
    private ApplymentUboInfo uboInfo;
}
