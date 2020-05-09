package chou.may.wechat.custom.sal.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 微信进件相关凭证
 * @author Lin.xc
 * @date 2020/02/14
 */
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WechatCertificate {

    /**
     * 商户号
     * */
    private String mchId;

    /**
     * 商户证书序列号
     * */
    private String mchSerialNo;

    /**
     * 商户私钥
     * */
    private String mchPrivateKey;

    /**
     * api密钥
     * */
    private String apiV3Key;

}
