package chou.may.mypakage.web.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 定制API分类
 * @author lin.xc
 * @date 2021/4/1
 **/
public enum TailorApiCategoryEnum {

    YS((byte)1,"有数接口");

    private Byte id;
    private String desc;

    TailorApiCategoryEnum(Byte id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public Byte getId() {
        return id;
    }

    public String getDesc() {
        return desc;
    }

    public static final Map<Byte, TailorApiCategoryEnum> VALUES = new HashMap<>();

    static {
        for (TailorApiCategoryEnum idxEnum : TailorApiCategoryEnum.values()) {
            VALUES.put(idxEnum.getId(), idxEnum);
        }
    }

    public static TailorApiCategoryEnum of(Byte id){
        return VALUES.get(id);
    }

}
