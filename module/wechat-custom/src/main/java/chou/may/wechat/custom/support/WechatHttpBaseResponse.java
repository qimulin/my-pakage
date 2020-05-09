package chou.may.wechat.custom.support;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 微信通道的基本响应
 * 【注意】微信接口成功和失败返回格式不一样，故自定义一个并集对象去接收，方便上层业务根据不同情况处理
 * @author Lin.xc
 * @date 2020/2/17
 */
@Data
@AllArgsConstructor
public class WechatHttpBaseResponse {
    /**
     * 响应数据（成功时返回）
     * */
    private int statusCode;
    /**
     * 内容
     * */
    private String content;
}
