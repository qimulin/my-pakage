package chou.may.wechat.custom.sal.request;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * 线下门店场景
 * @author Lin.xc
 * @date 2020/2/14
 * */
@Data
public class ApplymentBizStoreInfo {

    /**
     * 门店名称
     * */
    @JSONField(name = "biz_store_name")
    private String bizStoreName;
    /**
     * 门店省市编码
     * */
    @JSONField(name = "biz_address_code")
    private String bizAddressCode;
    /**
     * 门店地址
     * */
    @JSONField(name = "biz_store_address")
    private String bizStoreAddress;
    /**
     * 门店门头照片
     * */
    @JSONField(name = "store_entrance_pic")
    private List<String> storeEntrancePic;
    /**
     * 店内环境照片
     * */
    @JSONField(name = "indoor_pic")
    private List<String> indoorPic;
    /**
     * 线下场所对应的商家APPID
     * */
    @JSONField(name = "biz_sub_appid")
    private String bizSubAppid;

}
