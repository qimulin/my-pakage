package chou.may.wechat.custom.sal.request;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * 小程序场景
 * @author Lin.xc
 * @date 2020/2/14
 * */
@Data
public class ApplymentMiniProgramInfo {

    /**
     * 服务商小程序APPID
     * */
    @JSONField(name = "mini_program_appid")
    private String miniProgramAppid;
    /**
     * 商家小程序APPID
     * */
    @JSONField(name = "mini_program_sub_appid")
    private String miniProgramSubAppid;
    /**
     * 小程序截图
     * */
    @JSONField(name = "mini_program_pics")
    private List<String> miniProgramPics;

}
