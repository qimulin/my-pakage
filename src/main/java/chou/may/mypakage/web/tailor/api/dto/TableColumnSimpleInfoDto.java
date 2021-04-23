package chou.may.mypakage.web.tailor.api.dto;

import lombok.Data;

/**
 * 数据源表字段信息
 * @author lin.xc
 * @date 2021/2/24
 **/
@Data
public class TableColumnSimpleInfoDto {
    /** 名称 */
    private String name;
    /** 备注 */
    private String comment;
    /** 数据类型 */
    private String dataType;
}
