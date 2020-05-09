package chou.may.wechat.custom.support;

import com.alibaba.fastjson.JSON;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Validator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.UUID;

/**
 * @author Lin.xc
 * @date 2020/2/17
 */
@Slf4j
public class WechatMediaUploadUtils {

    /**
     * 上传媒体文件
     * @param uri 接口地址
     * @param fileName 文件名
     * @param fileBytes 文件字节数组
     * @param wechatMedia2Credentials 证书
     * @param wechatPay2Validator 验证器
     * */
    public static WechatHttpBaseResponse postMedia(String uri, String fileName, byte[] fileBytes,
                                                   WechatMedia2Credentials wechatMedia2Credentials,
                                                   WechatPay2Validator wechatPay2Validator
     ){
        log.info("POST Media接口请求，地址：[{}]，传入文件名：[{}]", uri, fileName);

        // 校验这些相关参数
        Assert.isTrue(!StringUtils.isEmpty(fileName),"fileName is blank");
        Assert.isTrue((fileBytes!=null && fileBytes.length>0), "fileBytes is empty");

        // 这个httpclient不好用微信官方包wechatpay-apache-httpclient里的，因为目前并不适用提交文件类型，具体可看源码
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        int status = -1;
        String content = null;
        try {
            // 读取文件摘要sha256
            String fileSha256 = DigestUtils.sha256Hex(fileBytes);
            WechatMediaMeta wechatMediaMeta = new WechatMediaMeta(fileName, fileSha256);
            HttpPost httpPost = new HttpPost(uri);
            // 设置头部
            httpPost.addHeader("Accept","application/json");
            httpPost.addHeader("Content-Type","multipart/form-data");
            // 这里只需要这么多就可以获取token了
            httpPost.addHeader("Authorization", wechatMedia2Credentials.getSchema() + " " + wechatMedia2Credentials.getMediaToken(httpPost,wechatMediaMeta));

            // 创建MultipartEntityBuilder
            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create().setMode(HttpMultipartMode.RFC6532);
            // 设置boundary
            multipartEntityBuilder.setBoundary(UUID.randomUUID().toString());
            multipartEntityBuilder.setCharset(Charset.forName("UTF-8"));
            // 设置meta内容
            multipartEntityBuilder.addTextBody("meta", JSON.toJSONString(wechatMediaMeta), ContentType.APPLICATION_JSON);
            // 设置图片内容
            multipartEntityBuilder.addBinaryBody("file", fileBytes, ContentType.create("image/jpg"), fileName);
            // 放入内容
            httpPost.setEntity(multipartEntityBuilder.build());

            // 获取返回内容
            response = httpclient.execute(httpPost);
            status = response.getStatusLine().getStatusCode();
            // 验签
            WechatHttpUtils.validateResponse(wechatPay2Validator, response);
            // 通过验签，处理返回结果
            HttpEntity entity = response.getEntity();
            content = WechatHttpUtils.getContent(entity);
            EntityUtils.consume(entity);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            WechatHttpUtils.closeResponse(response);
        }
        log.info("POST Media接口返回，状态码：[{}]，内容：[{}]", status, content);
        return new WechatHttpBaseResponse(status, content);
    }

}
