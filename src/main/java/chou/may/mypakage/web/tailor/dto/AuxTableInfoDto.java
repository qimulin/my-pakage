package chou.may.mypakage.web.tailor.dto;

import lombok.Data;

@Data
public class AuxTableInfoDto {

    private String tableName;

    private String note;

    private Byte isNeedCover;

    private String createScript;

}