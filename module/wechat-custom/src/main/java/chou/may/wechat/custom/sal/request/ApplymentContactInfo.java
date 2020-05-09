package chou.may.wechat.custom.sal.request;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 超级管理员信息
 * @author Lin.xc
 * @date 2020/2/14
 * */
@Data
public class ApplymentContactInfo {

    /**
     * 超级管理员姓名
     * */
    @JSONField(name = "contact_name")
    private String contactName;

    /**
     * 超级管理员身份证件号码
     * */
    @JSONField(name = "contact_id_number")
    private String contactIdNumber;

    /**
     * 超级管理员微信openid
     * */
    @JSONField(name = "openid")
    private String openid;

    /**
     * 联系手机
     * */
    @JSONField(name = "mobile_phone")
    private String mobilePhone;

    /**
     * 联系邮箱
     * */
    @JSONField(name = "contact_email")
    private String contactEmail;

}
