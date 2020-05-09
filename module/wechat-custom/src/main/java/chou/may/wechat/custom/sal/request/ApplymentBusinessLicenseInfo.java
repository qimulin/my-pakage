package chou.may.wechat.custom.sal.request;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 营业执照
 * @author Lin.xc
 * @date 2020/2/14
 * */
@Data
public class ApplymentBusinessLicenseInfo {

    /**
     * 营业执照照片
     * */
    @JSONField(name = "license_copy")
    private String licenseCopy;
    /**
     * 注册号/统一社会信用代码
     * */
    @JSONField(name = "license_number")
    private String licenseNumber;
    /**
     * 商户名称
     * */
    @JSONField(name = "merchant_name")
    private String merchantName;
    /**
     * 个体户经营者/法人姓名
     * */
    @JSONField(name = "legal_person")
    private String legalPerson;
}
