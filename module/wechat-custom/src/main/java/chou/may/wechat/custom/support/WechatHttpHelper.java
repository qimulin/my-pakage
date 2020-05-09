package chou.may.wechat.custom.support;


import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.auth.AutoUpdateCertificatesVerifier;
import com.wechat.pay.contrib.apache.httpclient.auth.PrivateKeySigner;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Validator;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.security.PrivateKey;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 微信Http帮助类
 * 【说明】主要是不想重复创建一些东西，影响性能
 * @author Lin.xc
 * @date 2020/2/22
 */
@Slf4j
@Component
public class WechatHttpHelper {

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

    /**
     * 媒体文件证书，仅供操作媒体文件时使用
     * */
    private WechatMedia2Credentials wechatMedia2Credentials;

    /**
     * 自定义自动更新证书验证器
     * */
    private CustomAutoUpdateCertificatesVerifier certificatesVerifier;

    /**
     * 微信http客户端
     * */
    private CloseableHttpClient wechatHttpClient;

    /**
     * 默认默认编码格式
     * */
    public static final String CHARSET_DEFAULT = "utf-8";

    private ReentrantLock lock = new ReentrantLock();

    /**
     * 检查证书进件相关请求参数
     * */
    private void checkCertificateParam(String mchId, String mchSerialNo, String mchPrivateKey, String apiV3Key){
        Assert.isTrue(!StringUtils.isEmpty(mchId), "mchId is blank");
        Assert.isTrue(!StringUtils.isEmpty(mchSerialNo), "mchSerialNo is blank");
        Assert.isTrue(!StringUtils.isEmpty(mchPrivateKey), "mchPrivateKey is blank");
        Assert.isTrue(!StringUtils.isEmpty(apiV3Key), "apiV3Key is blank");
    }

    /**
     * 检查证书进件相关请求参数是否相同
     * */
    private boolean checkCertificateParamSame(
            String mchId, String mchSerialNo, String mchPrivateKey, String apiV3Key){
        return (mchId.equals(this.mchId)
                && mchSerialNo.equals(this.mchSerialNo)
                && mchPrivateKey.equals(this.mchPrivateKey)
                && apiV3Key.equals(this.apiV3Key));
    }

    /**
     * 获取媒体文件证书
     * */
    public WechatMedia2Credentials getWechatMedia2Credentials(
            String mchId, String mchSerialNo, String mchPrivateKey, String apiV3Key)  {
        // 先检查参数不能传空白值
        checkCertificateParam(mchId, mchSerialNo, mchPrivateKey, apiV3Key);
        if(null!=wechatMedia2Credentials && checkCertificateParamSame(mchId, mchSerialNo, mchPrivateKey, apiV3Key)){
            log.info("WechatMedia2Credentials already exists, will return！");
            return wechatMedia2Credentials;
        }
        try {
            PrivateKey privateKey = PemUtil.loadPrivateKey(
                    new ByteArrayInputStream(mchPrivateKey.getBytes(CHARSET_DEFAULT)));
            this.wechatMedia2Credentials = new WechatMedia2Credentials(mchId, new WechatMediaSigner(mchSerialNo, privateKey));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
       return wechatMedia2Credentials;
    }


    /**
     * 获取证书验证器
     * */
    public CustomAutoUpdateCertificatesVerifier getCertificatesVerifier(
            String mchId, String mchSerialNo, String mchPrivateKey, String apiV3Key) {
        // 先检查参数不能传空白值
        checkCertificateParam(mchId, mchSerialNo, mchPrivateKey, apiV3Key);
        // 如果已经有wechatHttpClient值且参数没发生变化则直接返回
        if(null!=certificatesVerifier && checkCertificateParamSame(mchId, mchSerialNo, mchPrivateKey, apiV3Key)){
            log.info("Custom AutoUpdateCertificatesVerifier already exists, will return！");
            return certificatesVerifier;
        }
        PrivateKey privateKey = null;
        try {
            privateKey = PemUtil.loadPrivateKey(
                    new ByteArrayInputStream(mchPrivateKey.getBytes(CHARSET_DEFAULT)));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return getCustomAutoUpdateCertificatesVerifier(mchId, mchSerialNo, privateKey, apiV3Key);
    }

    /**
     * 获取证书验证器
     * 【注意】该方法不校验参数，请外层校验
     * */
    public CustomAutoUpdateCertificatesVerifier getCustomAutoUpdateCertificatesVerifier(
            String mchId, String mchSerialNo, PrivateKey privateKey, String apiV3Key) {
        try {
            this.certificatesVerifier = new CustomAutoUpdateCertificatesVerifier(
                    new WechatPay2Credentials(mchId, new PrivateKeySigner(mchSerialNo, privateKey)),
                    apiV3Key.getBytes(CHARSET_DEFAULT),
                    // 12小时更新
                    AutoUpdateCertificatesVerifier.TimeInterval.TwelveHours.getMinutes()
            );
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return certificatesVerifier;
    }

    /**
     * 构建微信HttpClient
     * */
    public CloseableHttpClient getWechatHttpClient(
            String mchId, String mchSerialNo, String mchPrivateKey, String apiV3Key){
        // 先检查参数不能传空白值
        checkCertificateParam(mchId, mchSerialNo, mchPrivateKey, apiV3Key);
        // 如果已经有wechatHttpClient值且参数没发生变化则直接返回
        if(null!=wechatHttpClient && checkCertificateParamSame(mchId, mchSerialNo, mchPrivateKey, apiV3Key)){
            log.info("WechatHttpClient already exists, will return！");
            return wechatHttpClient;
        }
        // 定义个CloseableHttpClient供可关闭
        CloseableHttpClient wechatHttpClient = null;
        try {
            PrivateKey privateKey = PemUtil.loadPrivateKey(
                    new ByteArrayInputStream(mchPrivateKey.getBytes(CHARSET_DEFAULT)));
            // 使用自动更新的签名验证器，不需要传入证书
            CustomAutoUpdateCertificatesVerifier verifier = getCustomAutoUpdateCertificatesVerifier(
                    mchId, mchSerialNo, privateKey, apiV3Key);

            WechatPayHttpClientBuilder builder = WechatPayHttpClientBuilder.create()
                    .withMerchant(mchId, mchSerialNo, privateKey)
                    .withValidator(new WechatPay2Validator(verifier));

            // 通过WechatPayHttpClientBuilder构造的HttpClient，会自动的处理签名和验签
            wechatHttpClient = builder.build();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Assert.notNull(wechatHttpClient,"WechatPayHttpClient is null");

        if (lock.tryLock()) {
            try {
                // 更新属性值
                this.wechatHttpClient = wechatHttpClient;
                this.mchId = mchId;
                this.mchSerialNo = mchSerialNo;
                this.mchPrivateKey = mchPrivateKey;
                this.apiV3Key = apiV3Key;
            } finally {
                lock.unlock();
            }
        }
        log.info("new WechatHttpClient has been created！");
        return wechatHttpClient;
    }

}
