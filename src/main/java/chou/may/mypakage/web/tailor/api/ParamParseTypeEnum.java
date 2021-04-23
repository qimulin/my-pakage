package chou.may.mypakage.web.tailor.api;

import java.util.HashMap;
import java.util.Map;

/**
 * 定制接口参数解析类型枚举
 * @author lin.xc
 * @date 2021/4/14
 **/
public enum ParamParseTypeEnum {

    ELEMENT((byte)1, "元素"),
    ARRAY((byte)2, "数组");

    private Byte id;
    private String desc;

    ParamParseTypeEnum(Byte id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public Byte getId() {
        return id;
    }

    public String getDesc() {
        return desc;
    }

    public static final Map<Byte, ParamParseTypeEnum> VALUES = new HashMap<>();

    static {
        for (ParamParseTypeEnum idxEnum : ParamParseTypeEnum.values()) {
            VALUES.put(idxEnum.getId(), idxEnum);
        }
    }

    public static ParamParseTypeEnum of(Byte id){
        return VALUES.get(id);
    }
}
