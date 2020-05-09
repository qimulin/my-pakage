package chou.may.wechat.custom.support;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Validator;
import com.wechat.pay.contrib.apache.httpclient.util.AesUtil;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;



/**
 * 微信直连Http请求工具类
 * @author Lin.xc
 * @date 2020/02/11
 */
@Slf4j
public class WechatHttpUtils {

    /**
     * 默认默认编码格式
     * */
    public static final String CHARSET_DEFAULT = "utf-8";

    /**
     * Post请求Json
     * @param wechatHttpClient 微信http客户端
     * @param uri 请求地址
     * @param param 参数对象
     * @param certificatesSerialNo 平台证书序列号
     * */
    public static WechatHttpBaseResponse postJson(
            CloseableHttpClient wechatHttpClient, String uri, Object param, String certificatesSerialNo)  {
        String paramString = JSON.toJSONString(param);
        return postJson(wechatHttpClient, uri, paramString, certificatesSerialNo);
    }

    /**
     * Post请求Json
     * @param wechatHttpClient 微信http客户端
     * @param uri 请求地址
     * @param paramString 参数对象字符串
     * @param certificatesSerialNo 平台证书序列号
     * */
    public static WechatHttpBaseResponse postJson(
            CloseableHttpClient wechatHttpClient, String uri, String paramString, String certificatesSerialNo)  {
        log.info("POST Json接口请求，地址：[{}]，传入参数：[{}]", uri, paramString);
        CloseableHttpResponse response = null;
        int status = -1;
        String content = null;
        try {
            HttpPost httpPost = new HttpPost(uri);
            if(!StringUtils.isEmpty(paramString)){
                InputStream stream = new ByteArrayInputStream(paramString.getBytes(CHARSET_DEFAULT));
                InputStreamEntity reqEntity = new InputStreamEntity(stream);
                reqEntity.setContentType("application/json");
                httpPost.setEntity(reqEntity);
                httpPost.addHeader("Accept", "application/json");
                // 有敏感信息的需要
                if(!StringUtils.isEmpty(certificatesSerialNo)){
                    httpPost.addHeader("Wechatpay-Serial", certificatesSerialNo);
                }
            }
            response = wechatHttpClient.execute(httpPost);
            status = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            content = getContent(entity);
            EntityUtils.consume(entity);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeResponse(response);
        }
        log.info("POST Json接口返回，状态码：[{}]，内容：[{}]", status, content);
        return new WechatHttpBaseResponse(status, content);
    }

    /**
     * Post请求Json
     * @param wechatHttpClient 微信http客户端
     * @param uri 请求地址
     * */
    public static WechatHttpBaseResponse getJson(
            CloseableHttpClient wechatHttpClient, String uri)  {
        log.info("GET接口请求，地址：[{}]", uri);
        CloseableHttpResponse response = null;
        int status = -1;
        String content = null;
        try {
            URIBuilder uriBuilder = new URIBuilder(uri);
            HttpGet httpGet = new HttpGet(uriBuilder.build());
            httpGet.addHeader("Accept", "application/json");
            response = wechatHttpClient.execute(httpGet);
            status = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            content = getContent(entity);
            EntityUtils.consume(entity);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } finally {
            closeResponse(response);
        }
        log.info("GET接口返回，状态码：[{}]，内容：[{}]", status, content);
        return new WechatHttpBaseResponse(status, content);
    }

    /************************************************* 辅助方法 ****************************************************/
    /**
     * 根据响应获取响应实体
     * 注意：请外层自行关资源
     * @param response
     * @return
     */
    public static String getContent(CloseableHttpResponse response) {
        HttpEntity entity = response.getEntity();
        return getContent(entity);
    }

    /**
     * 根据响应获取响应实体
     * 注意：请外层自行关资源
     * @param entity
     * @return
     */
    public static String getContent(HttpEntity entity) {
        String content = Strings.EMPTY;
        if(null==entity){
            return content;
        }
        try {
            content = EntityUtils.toString(entity, CHARSET_DEFAULT);
        } catch (IOException e) {
            // output("解析响应实体时java IO 异常！", e1);
            e.printStackTrace();
        }
        return content;
    }

    /**
     * 关闭响应
     * @param response
     * @return
     */
    public static void closeResponse(CloseableHttpResponse response) {
        if(null!=response){
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取拼接的请求地址
     * */
    public static String getApiUri(String domain, String...urlParts){
        String result = domain;
        for (String p : urlParts){
            result = result.concat(p);
        }
        return result;
    }

    /**
     * 对响应进行验签
     * */
    public static void validateResponse(WechatPay2Validator validator, CloseableHttpResponse response) throws IOException {
        // 对成功应答验签
        StatusLine statusLine = response.getStatusLine();
        convertToRepeatableResponseEntity(response);
        if (statusLine.getStatusCode() >= 200 && statusLine.getStatusCode() < 300) {
            if (!validator.validate(response)) {
                throw new RuntimeException("应答的微信支付签名验证失败");
            }
        }
    }

    protected static HttpEntity newRepeatableEntity(HttpEntity entity) throws IOException {
        byte[] content = EntityUtils.toByteArray(entity);
        ByteArrayEntity newEntity = new ByteArrayEntity(content);
        newEntity.setContentEncoding(entity.getContentEncoding());
        newEntity.setContentType(entity.getContentType());

        return newEntity;
    }

    protected static void convertToRepeatableResponseEntity(CloseableHttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        if (entity != null && !entity.isRepeatable()) {
            response.setEntity(newRepeatableEntity(entity));
        }
    }

    /**
     * 反序列化证书并解密
     */
    public static List<X509Certificate> deserializeToCerts(byte[] apiV3Key, String body)
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

}
