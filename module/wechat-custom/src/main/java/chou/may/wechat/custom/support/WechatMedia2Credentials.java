package chou.may.wechat.custom.support;

import com.alibaba.fastjson.JSON;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.IOException;
import java.net.URI;

/**
 * 主要参考com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials
 * 因为觉得这里面有些方法写的挺好，但是针对图片上传又不适用，所以想拿出来改动下
 * @author Lin.xc
 * @date 2020/02/21
 */
@Slf4j
public class WechatMedia2Credentials extends WechatPay2Credentials {

  protected WechatMediaSigner signer;

  public WechatMedia2Credentials(String merchantId, WechatMediaSigner signer) {
    super(merchantId, signer);
    this.signer=signer;
  }

  public final String getMediaToken(HttpUriRequest request, WechatMediaMeta meta) throws IOException {
    String nonceStr = generateNonceStr();
    long timestamp = generateTimestamp();

    String message = buildMediaMessage(nonceStr, timestamp, request.getURI(), request.getRequestLine().getMethod(), meta);
    log.debug("authorization message=[{}]", message);

    WechatMediaSigner.MediaSignatureResult signature = signer.signMedia(message.getBytes("utf-8"));

    String token = "mchid=\"" + getMerchantId() + "\","
        + "nonce_str=\"" + nonceStr + "\","
        + "timestamp=\"" + timestamp + "\","
        + "serial_no=\"" + signature.certificateSerialNumber + "\","
        + "signature=\"" + signature.sign + "\"";
    log.debug("authorization token=[{}]", token);

    return token;
  }

  protected final String buildMediaMessage(String nonce, long timestamp, URI uri, String method, WechatMediaMeta meta)
      throws IOException {
    String canonicalUrl = uri.getRawPath();
    if (uri.getQuery() != null) {
      canonicalUrl += "?" + uri.getRawQuery();
    }
    return method + "\n"
        + canonicalUrl + "\n"
        + timestamp + "\n"
        + nonce + "\n"
        + JSON.toJSONString(meta) + "\n";
  }

}
