package chou.may.mypakage.web.constant;

import chou.may.mypakage.web.annotation.EnumFieldWebReturn;
import chou.may.mypakage.web.annotation.EnumWebReturn;

import java.util.HashMap;
import java.util.Map;

/**
 * 公用是否枚举
 * @author lin.xc
 * @date 2020-07-03
 * */
@EnumWebReturn(name = "CommonIs")
public enum CommonIsEnum {

    YES(1, (byte)1,"Y","是"),
    NO(0, (byte)0,"N","否");

    @EnumFieldWebReturn(name = "key")
    private int code;
    private byte tinyCode;
    private String value;
    @EnumFieldWebReturn(name = "value")
    private String name;

    CommonIsEnum(int code, byte tinyCode, String value, String name) {
        this.code = code;
        this.tinyCode = tinyCode;
        this.value = value;
        this.name = name;
    }
    public static final Map<String, CommonIsEnum> VALUES = new HashMap<>();

    static {
        for (CommonIsEnum colEnum : CommonIsEnum.values()) {
            VALUES.put(String.valueOf(colEnum.code), colEnum);
        }
    }
    public int getCode() {
        return this.code;
    }

    public byte getTinyCode() {
        return this.tinyCode;
    }

    public String getValue() {
        return this.value;
    }

    public String getName() {
        return this.name;
    }

    public static String getEnum(String code){
        return VALUES.get(code).name;
    }

}
