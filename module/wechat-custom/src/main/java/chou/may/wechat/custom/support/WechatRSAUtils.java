package chou.may.wechat.custom.support;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Base64;

/**
 * RSA工具类
 * @author Lin.xc
 * @date 2020/02/11
 */
@Slf4j
public class WechatRSAUtils {

     // 加密算法
    private static final String CIPHER_ALGORITHM = "RSA/ECB/OAEPWithSHA-1AndMGF1Padding";

    /**
     * 加密
     * @param message 加密信息
     * @param certificate 平台有效证书
     * */
    public static String rsaEncryptOAEP(String message, X509Certificate certificate){
        String cipherText = null;
        try {
            cipherText = toRsaEncryptOAEP(message, certificate);
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cipherText;
    }

    /**
     * 加密
     * */
    private static String toRsaEncryptOAEP(String message, X509Certificate certificate)
            throws IllegalBlockSizeException, IOException {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, certificate.getPublicKey());

            byte[] data = message.getBytes("utf-8");
            byte[] cipherData = cipher.doFinal(data);
            String cipherText = Base64.getEncoder().encodeToString(cipherData);
            log.info("微信进件敏感信息加密，原文为[{}]，密文为[{}]", message, cipherText);
            return cipherText;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("当前Java环境不支持RSA v1.5/OAEP", e);
        } catch (InvalidKeyException e) {
            throw new IllegalArgumentException("无效的证书", e);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new IllegalBlockSizeException("加密原串的长度不能超过214字节");
        }
    }

    /**
     * 解密
     * */
    public static String toRsaDecryptOAEP(String ciphertext, PrivateKey privateKey)
            throws BadPaddingException, IOException {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            byte[] data = Base64.getDecoder().decode(ciphertext);
            return new String(cipher.doFinal(data), "utf-8");
        } catch (NoSuchPaddingException | NoSuchAlgorithmException e) {
            throw new RuntimeException("当前Java环境不支持RSA v1.5/OAEP", e);
        } catch (InvalidKeyException e) {
            throw new IllegalArgumentException("无效的私钥", e);
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
            throw new BadPaddingException("解密失败");
        }
    }

}
