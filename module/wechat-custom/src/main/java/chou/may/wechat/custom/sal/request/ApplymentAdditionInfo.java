package chou.may.wechat.custom.sal.request;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * 补充材料
 * @author Lin.xc
 * @date 2020/2/14
 * */
@Data
public class ApplymentAdditionInfo {
    /**
     * 法人开户承诺函
     * */
    @JSONField(name = "legal_person_commitment")
    private String legalPersonCommitment;
    /**
     * 法人开户意愿视频
     * */
    @JSONField(name = "legal_person_video")
    private String legalPersonVideo;
    /**
     * 补充材料
     * */
    @JSONField(name = "business_addition_pics")
    private List<String> businessAdditionPics;
    /**
     * 补充说明
     * */
    @JSONField(name = "business_addition_msg")
    private String businessAdditionMsg;
}
