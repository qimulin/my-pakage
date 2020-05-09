package chou.may.wechat.custom.support;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wechat.pay.contrib.apache.httpclient.Credentials;
import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.auth.AutoUpdateCertificatesVerifier;
import com.wechat.pay.contrib.apache.httpclient.auth.CertificatesVerifier;
import com.wechat.pay.contrib.apache.httpclient.auth.Verifier;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Validator;
import com.wechat.pay.contrib.apache.httpclient.util.AesUtil;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 自定义AutoUpdateCertificatesVerifier，参考微信官方包中的AutoUpdateCertificatesVerifier
 * 【说明】官方包很多方法写的很好，但就是太隐蔽（私有）了，所以拷出来改下用下
 * @author Lin.xc
 * @date 2020/02/24
 * 在原有CertificatesVerifier基础上，增加自动更新证书功能
 */
public class CustomAutoUpdateCertificatesVerifier implements Verifier {

  private static final Logger log = LoggerFactory.getLogger(AutoUpdateCertificatesVerifier.class);

  //证书下载地址
  private static final String CertDownloadPath = "https://api.mch.weixin.qq.com/v3/certificates";

  //上次更新时间
  private volatile Instant instant;

  //证书更新间隔时间，单位为分钟
  private int minutesInterval;

  private CertificatesVerifier verifier;

  private Credentials credentials;

  private byte[] apiV3Key;

  private ReentrantLock lock = new ReentrantLock();

  /**
   * 添加该属性
   * */
  private List<X509Certificate> x509Certificates;

  //时间间隔枚举，支持一小时、六小时以及十二小时
  public enum TimeInterval {
    OneHour(60), SixHours(60 * 6), TwelveHours(60 * 12);

    private int minutes;

    TimeInterval(int minutes) {
      this.minutes = minutes;
    }

    public int getMinutes() {
      return minutes;
    }
  }

  public CustomAutoUpdateCertificatesVerifier(Credentials credentials, byte[] apiV3Key) {
    this(credentials, apiV3Key, TimeInterval.OneHour.getMinutes());
  }

  public CustomAutoUpdateCertificatesVerifier(Credentials credentials, byte[] apiV3Key, int minutesInterval) {
    this.credentials = credentials;
    this.apiV3Key = apiV3Key;
    this.minutesInterval = minutesInterval;
    //构造时更新证书
    try {
      autoUpdateCert();
      instant = Instant.now();
    } catch (IOException | GeneralSecurityException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public boolean verify(String serialNumber, byte[] message, String signature) {
    checkAutoUpdateCert();
    return verifier.verify(serialNumber, message, signature);
  }

  /**
   * 检查自动更新
   * */
  private void checkAutoUpdateCert(){
    if (instant == null || Duration.between(instant, Instant.now()).toMinutes() >= minutesInterval) {
      if (lock.tryLock()) {
        try {
          autoUpdateCert();
          //更新时间
          instant = Instant.now();
        } catch (GeneralSecurityException | IOException e) {
          log.warn("Auto update cert failed, exception = " + e);
        } finally {
          lock.unlock();
        }
      }
    }
  }

  private void autoUpdateCert() throws IOException, GeneralSecurityException {
    CloseableHttpClient httpClient = WechatPayHttpClientBuilder.create()
        .withCredentials(credentials)
        .withValidator(verifier == null ? (response) -> true : new WechatPay2Validator(verifier))
        .build();

    HttpGet httpGet = new HttpGet(CertDownloadPath);
    httpGet.addHeader("Accept", "application/json");

    CloseableHttpResponse response = httpClient.execute(httpGet);
    int statusCode = response.getStatusLine().getStatusCode();
    String body = EntityUtils.toString(response.getEntity());
    if (statusCode == 200) {
      List<X509Certificate> newCertList = deserializeToCerts(apiV3Key, body);
      if (newCertList.isEmpty()) {
        log.warn("Cert list is empty");
        return;
      }
      // 更新证书
      this.x509Certificates = newCertList;
      // 更新验证器
      this.verifier = new CertificatesVerifier(newCertList);
    } else {
      log.warn("Auto update cert failed, statusCode = " + statusCode + ",body = " + body);
    }
  }


  /**
   * 反序列化证书并解密
   */
  private List<X509Certificate> deserializeToCerts(byte[] apiV3Key, String body)
      throws GeneralSecurityException, IOException {
    AesUtil decryptor = new AesUtil(apiV3Key);
    ObjectMapper mapper = new ObjectMapper();
    JsonNode dataNode = mapper.readTree(body).get("data");
    List<X509Certificate> newCertList = new ArrayList<>();
    if (dataNode != null) {
      for (int i = 0, count = dataNode.size(); i < count; i++) {
        JsonNode encryptCertificateNode = dataNode.get(i).get("encrypt_certificate");
        //解密
        String cert = decryptor.decryptToString(
            encryptCertificateNode.get("associated_data").toString().replaceAll("\"", "")
                .getBytes("utf-8"),
            encryptCertificateNode.get("nonce").toString().replaceAll("\"", "")
                .getBytes("utf-8"),
            encryptCertificateNode.get("ciphertext").toString().replaceAll("\"", ""));

        X509Certificate x509Cert = PemUtil
            .loadCertificate(new ByteArrayInputStream(cert.getBytes("utf-8")));
        try {
          x509Cert.checkValidity();
        } catch (CertificateExpiredException | CertificateNotYetValidException e) {
          continue;
        }
        newCertList.add(x509Cert);
      }
    }
    return newCertList;
  }

  /**
   * 新增方法，获取解析后的证书
   */
  public List<X509Certificate> getX509Certificates(){
    if ((instant == null || Duration.between(instant, Instant.now()).toMinutes() >= minutesInterval)
            || CollectionUtils.isEmpty(this.x509Certificates)
    ) {
      // 该种情况需要去更新
      if (lock.tryLock()) {
        try {
          autoUpdateCert();
          //更新时间
          instant = Instant.now();
        } catch (GeneralSecurityException | IOException e) {
          log.warn("Auto update cert failed, exception = " + e);
        } finally {
          lock.unlock();
        }
      }
    }
    return this.x509Certificates;
  }

  /**
   * 获取验证器
   * */
  public CertificatesVerifier getCertificatesVerifier(){
    return this.verifier;
  }

}
