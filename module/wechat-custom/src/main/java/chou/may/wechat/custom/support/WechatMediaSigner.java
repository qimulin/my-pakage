package chou.may.wechat.custom.support;

import com.wechat.pay.contrib.apache.httpclient.auth.PrivateKeySigner;
import lombok.Getter;

import java.security.*;
import java.util.Base64;

/**
 * 主要参考com.wechat.pay.contrib.apache.httpclient.auth.Signer
 * @author Lin.xc
 * @date 2020/02/21
 */
@Getter
public class WechatMediaSigner extends PrivateKeySigner {
  /**
   * 父类取不到这两个属性，我就自定义，然后在构造函数中赋值
   * */
  private String serialNumber;
  private PrivateKey privateKey;

  public WechatMediaSigner(String serialNumber, PrivateKey privateKey) {
    super(serialNumber, privateKey);
    this.serialNumber=serialNumber;
    this.privateKey=privateKey;
  }

  public MediaSignatureResult signMedia(byte[] message) {
    try {
      Signature sign = Signature.getInstance("SHA256withRSA");
      sign.initSign(this.privateKey);
      sign.update(message);
      return new MediaSignatureResult(Base64.getEncoder().encodeToString(sign.sign()), this.serialNumber);
    } catch (NoSuchAlgorithmException var3) {
      throw new RuntimeException("当前Java环境不支持SHA256withRSA", var3);
    } catch (SignatureException var4) {
      throw new RuntimeException("签名计算失败", var4);
    } catch (InvalidKeyException var5) {
      throw new RuntimeException("无效的私钥", var5);
    }
  }

  public static class MediaSignatureResult {
    String sign;
    String certificateSerialNumber;

    public MediaSignatureResult(String sign, String serialNumber) {
      this.sign = sign;
      this.certificateSerialNumber = serialNumber;
    }
  }

}
