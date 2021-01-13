package chou.may.mypakage.web.constant;

import chou.may.mypakage.web.annotation.EnumFieldWebReturn;
import chou.may.mypakage.web.annotation.EnumWebReturn;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 公用是否枚举
 * @author lin.xc
 * @date 2020-07-03
 * */
@EnumWebReturn("CommonIs")
public enum CommonIsEnum {

    YES(1,"Y","是"),
    NO(0,"N","否");

    @EnumFieldWebReturn("key")
    private int code;
    private String value;
    @EnumFieldWebReturn("value")
    private String name;

    CommonIsEnum(int code, String value, String name) {
        this.code = code;
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
