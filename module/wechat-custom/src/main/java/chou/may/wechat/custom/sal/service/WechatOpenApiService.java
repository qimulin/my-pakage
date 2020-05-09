package chou.may.wechat.custom.sal.service;

import chou.may.wechat.custom.sal.request.ApplymentRequest;
import chou.may.wechat.custom.sal.request.ModifySettlementRequest;
import chou.may.wechat.custom.sal.response.*;

import java.security.cert.X509Certificate;

/**
 * @author: Lin.xc
 * @date: 2020/2/14
 * @description:微信第三方api请求类
 */
public interface WechatOpenApiService {

    /**
     * 获取有效的X509证书
     * @param wechatCertificate 微信证书凭证
     * */
    X509Certificate getIntervalX509Certificate(WechatCertificate wechatCertificate);

    /**
     * 上传图片
     * @param fileName 文件名
     * @param fileBytes 文件字节流
     * @param wechatCertificate 微信证书凭证
     * */
    BaseResponse<MediaUploadResponse> uploadImage(String fileName, byte[] fileBytes, WechatCertificate wechatCertificate);

    /**
     * 提交申请单
     * @param request 申请单请求
     * @param wechatCertificate 微信证书凭证
     * */
    BaseResponse<ApplymentResponse> applyment(ApplymentRequest request, WechatCertificate wechatCertificate);

    /**
     * 通过业务申请编号查询申请状态
     * @param businessCode 业务申请编号
     * @param wechatCertificate 微信证书凭证
     * */
    BaseResponse<GetApplymentResponse> getApplymentByBusinessCode(String businessCode, WechatCertificate wechatCertificate);

    /**
     * 通过申请单号查询申请状态
     * @param applymentId 申请单号
     * @param wechatCertificate 微信证书凭证
     * */
    BaseResponse<GetApplymentResponse> getApplymentByApplymentId(String applymentId, WechatCertificate wechatCertificate);

    /**
     * 修改结算帐号
     * @param subMchid 本服务商进件、已签约的特约商户号
     * @param request 修改请求
     * @param wechatCertificate 微信证书凭证
     * */
    BaseResponse modifySettlement(String subMchid, ModifySettlementRequest request, WechatCertificate wechatCertificate);

    /**
     * 修改结算帐号
     * @param subMchid 本服务商进件、已签约的特约商户号
     * @param wechatCertificate 微信证书凭证
     * */
    BaseResponse<GetSettlementResponse> getSettlement(String subMchid, WechatCertificate wechatCertificate);
}
