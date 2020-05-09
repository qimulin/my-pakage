package chou.may.wechat.custom.support;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 微信媒体文件元素
 * @author Lin.xc
 * @date 2020/2/20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WechatMediaMeta {
    /**
     * 商户上传的媒体图片的名称，商户自定义，必须以JPG、BMP、PNG为后缀
     * */
    @JSONField(name = "filename")
    private String fileName;
    /**
     * 参与签名计算的请求主体为meta的json串
     * */
    @JSONField(name = "sha256")
    private String sha256;
}
