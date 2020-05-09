package chou.may.wechat.custom.sal.response;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 媒体文件上传响应
 * @author Lin.xc
 * @date 2020/2/17
 */
@Data
public class MediaUploadResponse {
    /**
     * 媒体文件标识Id
     * */
    @JSONField(name  = "media_id")
    private String mediaId;
}
