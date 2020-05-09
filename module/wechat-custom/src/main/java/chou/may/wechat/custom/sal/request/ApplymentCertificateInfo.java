package chou.may.wechat.custom.sal.request;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 登记证书
 * @author Lin.xc
 * @date 2020/2/14
 * */
@Data
public class ApplymentCertificateInfo {

    /**
     * 登记证书照片
     * */
    @JSONField(name = "cert_copy")
    private String certCopy;

    /**
     * 登记证书类型
     * */
    @JSONField(name = "cert_type")
    private String certType;

    /**
     * 证书号
     * */
    @JSONField(name = "cert_number")
    private String certNumber;

    /**
     * 商户名称
     * */
    @JSONField(name = "merchant_name")
    private String merchantName;

    /**
     * 注册地址
     * */
    @JSONField(name = "company_address")
    private String companyAddress;

    /**
     * 法定代表人
     * */
    @JSONField(name = "legal_person")
    private String legalPerson;

    /**
     * 有效期限开始日期
     * */
    @JSONField(name = "period_begin")
    private String periodBegin;

    /**
     * 有效期限结束日期
     * */
    @JSONField(name = "period_end")
    private String periodEnd;

}
