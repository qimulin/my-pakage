package chou.may.mypakage.web.tailor.dto;

import lombok.Data;

/**
 * 数据源表信息
 * @author lin.xc
 * @date 2021/2/24
 **/
@Data
public class TableSimpleInfoDto {
    /** 名称 */
    private String name;
    /** 备注 */
    private String comment;
}
