package chou.may.mypakage.web.constant;

import chou.may.mypakage.web.annotation.EnumWebReturn;

/**
 * 公用是否枚举
 * @author lin.xc
 * @date 2020-07-03
 * */
@EnumWebReturn(name = "CommonIs")
public enum StatusEnum {

    SUCCESS("success","成功"),
    ERROR("error","错误");

    private String code;
    private String desc;

    StatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
