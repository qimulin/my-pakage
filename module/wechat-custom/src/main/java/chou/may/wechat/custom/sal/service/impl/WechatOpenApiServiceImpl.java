package chou.may.wechat.custom.sal.service.impl;

import chou.may.wechat.custom.sal.request.ApplymentRequest;
import chou.may.wechat.custom.sal.request.ModifySettlementRequest;
import chou.may.wechat.custom.sal.response.*;
import chou.may.wechat.custom.sal.service.WechatCertificate;
import chou.may.wechat.custom.sal.service.WechatOpenApiService;
import chou.may.wechat.custom.support.*;
import com.alibaba.fastjson.JSON;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Validator;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigInteger;
import java.security.cert.X509Certificate;
import java.util.List;

/**
 * @author: Lin.xc
 * @date: 2020/2/14
 */
@Slf4j
@Service
public class WechatOpenApiServiceImpl implements WechatOpenApiService {

    @Value(value = "${wechat.mchApiDomain}")
    private String mchApidomain;

    @Autowired
    private WechatHttpHelper wechatHttpHelper;

    private static final String API_MEDIA_UPLOAD= "/v3/merchant/media/upload";
    private static final String API_APPLYMENT = "/v3/applyment4sub/applyment/";
    private static final String API_GET_APPLYMENT_BY_BUSINESS_CODE ="/v3/applyment4sub/applyment/business_code/";
    private static final String API_GET_APPLYMENT_BY_APPLYMENT_ID ="/v3/applyment4sub/applyment/applyment_id/";
    private static final String API_MODIFY_SETTLEMENT = "/v3/apply4sub/sub_merchants/";
    private static final String API_MODIFY_SETTLEMENT_END = "/modify-settlement";
    private static final String API_GET_SETTLEMENT = "/v3/apply4sub/sub_merchants/";
    private static final String API_GET_SETTLEMENT_END = "/settlement";

    @Override
    public X509Certificate getIntervalX509Certificate(WechatCertificate wechatCertificate) {
        CustomAutoUpdateCertificatesVerifier certificatesVerifier = getCertificatesVerifier(wechatCertificate);
        List<X509Certificate> x509Certificates = certificatesVerifier.getX509Certificates();
        // 有效期最晚的则为有效证书
        X509Certificate enableX509Certificate = getLatestIntervalX509Certificate(x509Certificates);
        Assert.notNull(enableX509Certificate, "X509Certificate can not be null");
        return enableX509Certificate;
    }

    @Override
    public BaseResponse<MediaUploadResponse> uploadImage(String fileName, byte[] fileBytes, WechatCertificate wechatCertificate) {
        // 证书
        WechatMedia2Credentials wechatMedia2Credentials = getWechatMedia2Credentials(wechatCertificate);
        // 验证
        WechatPay2Validator wechatPay2Validator = getWechatPay2Validator(wechatCertificate);
        WechatHttpBaseResponse httpResponse = WechatMediaUploadUtils.postMedia(
                WechatHttpUtils.getApiUri(mchApidomain, API_MEDIA_UPLOAD), fileName, fileBytes,
                wechatMedia2Credentials, wechatPay2Validator);
        return toBaseResponse(httpResponse, MediaUploadResponse.class);
    }

    @Override
    public BaseResponse<ApplymentResponse> applyment(ApplymentRequest request, WechatCertificate wechatCertificate) {
        CloseableHttpClient wechatHttpClient = getWechatHttpClient(wechatCertificate);
        WechatHttpBaseResponse httpResponse = WechatHttpUtils.postJson(
                wechatHttpClient, WechatHttpUtils.getApiUri(mchApidomain, API_APPLYMENT),
                request, getPlatformCertificatesSerialNo(wechatCertificate)
        );
        return toBaseResponse(httpResponse, ApplymentResponse.class);
    }

    @Override
    public BaseResponse<GetApplymentResponse> getApplymentByBusinessCode(String businessCode, WechatCertificate wechatCertificate) {
        CloseableHttpClient wechatHttpClient = getWechatHttpClient(wechatCertificate);
        String uri = WechatHttpUtils.getApiUri(mchApidomain, API_GET_APPLYMENT_BY_BUSINESS_CODE, businessCode);
        WechatHttpBaseResponse httpResponse = WechatHttpUtils.getJson(wechatHttpClient,uri);
        return toBaseResponse(httpResponse,GetApplymentResponse.class);
    }

    @Override
    public BaseResponse<GetApplymentResponse> getApplymentByApplymentId(String applymentId, WechatCertificate wechatCertificate) {
        CloseableHttpClient wechatHttpClient = getWechatHttpClient(wechatCertificate);
        String uri = WechatHttpUtils.getApiUri(mchApidomain, API_GET_APPLYMENT_BY_APPLYMENT_ID, applymentId);
        WechatHttpBaseResponse httpResponse = WechatHttpUtils.getJson(wechatHttpClient, uri);
        return toBaseResponse(httpResponse,GetApplymentResponse.class);
    }

    @Override
    public BaseResponse modifySettlement(String subMchid, ModifySettlementRequest request, WechatCertificate wechatCertificate) {
        CloseableHttpClient wechatHttpClient = getWechatHttpClient(wechatCertificate);
        String uri = WechatHttpUtils.getApiUri(mchApidomain, API_MODIFY_SETTLEMENT, subMchid, API_MODIFY_SETTLEMENT_END);
        WechatHttpBaseResponse httpResponse
                = WechatHttpUtils.postJson(wechatHttpClient, uri, request, getPlatformCertificatesSerialNo(wechatCertificate));
        return toBaseResponse(httpResponse,GetApplymentResponse.class);
    }

    @Override
    public BaseResponse<GetSettlementResponse> getSettlement(String subMchid, WechatCertificate wechatCertificate) {
        CloseableHttpClient wechatHttpClient = getWechatHttpClient(wechatCertificate);
        String uri = WechatHttpUtils.getApiUri(mchApidomain, API_GET_SETTLEMENT, subMchid, API_GET_SETTLEMENT_END);
        WechatHttpBaseResponse httpResponse = WechatHttpUtils.getJson(wechatHttpClient, uri);
        return toBaseResponse(httpResponse,GetSettlementResponse.class);
    }

    /*************************************************** 辅助方法 ****************************************************/
    /**
     * 转BaseResponse
     * */
    private <T> BaseResponse<T> toBaseResponse(WechatHttpBaseResponse httpBaseResponse, Class<T> dataClazz){
        if (log.isDebugEnabled()) {
            log.debug("转微信BaseResponse，原参数：[{}]，data类型：[{}]",
                    JSON.toJSONString(httpBaseResponse), dataClazz);
        }
        BaseResponse baseResponse = new BaseResponse<T>();
        int statusCode = httpBaseResponse.getStatusCode();
        String content = httpBaseResponse.getContent();
        if(statusCode==200){
            baseResponse.setCode(BaseResponse.CODE_SUCCESS);
            baseResponse.setMessage("请求处理成功");
            if(null!=dataClazz){
                baseResponse.setData(JSON.parseObject(content,dataClazz));
            }
        }else if(statusCode==204){
            baseResponse.setCode(BaseResponse.CODE_SUCCESS);
            baseResponse.setMessage("请求处理成功，无数据返回");
        }else if(statusCode==202){
            baseResponse = JSON.parseObject(content, BaseResponse.class);
        }else if(statusCode==500 || statusCode==501 || statusCode==502 || statusCode==503){
            baseResponse = JSON.parseObject(content, BaseResponse.class);
        }else{
            // 用除以100的商判断什么code码开头
            int quotient = statusCode/100;
            if(quotient==4 && !StringUtils.isEmpty(content)){
                baseResponse = JSON.parseObject(content, BaseResponse.class);
            }else{
                // 其他，未知
                baseResponse.setCode(BaseResponse.CODE_UNKNOWN);
                baseResponse.setMessage("未知错误，http status：".concat(String.valueOf(statusCode)).concat(" content：").concat(content));
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("转微信BaseResponse，转换后：[{}]", JSON.toJSONString(baseResponse));
        }
        return baseResponse;
    }

    /**
     * 获取微信Http媒体文件证书器
     * */
    private WechatMedia2Credentials getWechatMedia2Credentials(WechatCertificate wechatCertificate){
         return wechatHttpHelper.getWechatMedia2Credentials(
                wechatCertificate.getMchId(),
                wechatCertificate.getMchSerialNo(),
                wechatCertificate.getMchPrivateKey(),
                wechatCertificate.getApiV3Key()
        );
    }

    /**
     * 获取微信Http客户端
     * */
    private CloseableHttpClient getWechatHttpClient(WechatCertificate wechatCertificate){
        return wechatHttpHelper.getWechatHttpClient(
                wechatCertificate.getMchId(),
                wechatCertificate.getMchSerialNo(),
                wechatCertificate.getMchPrivateKey(),
                wechatCertificate.getApiV3Key()
        );
    }

    /**
     * 获取微信证书验证器
     * */
    private CustomAutoUpdateCertificatesVerifier getCertificatesVerifier(WechatCertificate wechatCertificate){
        return wechatHttpHelper.getCertificatesVerifier(
                wechatCertificate.getMchId(),
                wechatCertificate.getMchSerialNo(),
                wechatCertificate.getMchPrivateKey(),
                wechatCertificate.getApiV3Key()
        );
    }

    /**
     * 获取微信验证器
     * */
    private WechatPay2Validator getWechatPay2Validator(WechatCertificate wechatCertificate){
        CustomAutoUpdateCertificatesVerifier certificatesVerifier = getCertificatesVerifier(wechatCertificate);
        return new WechatPay2Validator(certificatesVerifier.getCertificatesVerifier());
    }

    /**
     * 获取最晚有效的证书
     * */
    private X509Certificate getLatestIntervalX509Certificate(List<X509Certificate> x509Certificates){
        X509Certificate result = null;
        if(!CollectionUtils.isEmpty(x509Certificates)){
            result = x509Certificates.get(0);
        }
        int size = x509Certificates.size();
        // 若多组，取有效时间最晚的证书
        if(size>1){
            for(int i=1; i<size; i++){
                if((x509Certificates.get(i).getNotAfter()).after(result.getNotAfter())){
                    result = x509Certificates.get(i);
                }
            }
        }
        return result;
    }

    /**
     * 获取微信平台证书序列号
     * */
    private String getPlatformCertificatesSerialNo(WechatCertificate wechatCertificate){
        X509Certificate x509Certificate = getIntervalX509Certificate(wechatCertificate);
        BigInteger serialNumber = x509Certificate.getSerialNumber();
        return serialNumber.toString(16).toUpperCase();
    }


}
