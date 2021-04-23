package chou.may.mypakage.web.service;

import chou.may.mypakage.web.tailor.api.dto.AuxTableInfoDto;

import java.util.List;
import java.util.Map;

/**
 * 辅助表信息服务
 * TODO：本例子需要实现
 * @author lin.xc
 * @date 2021/4/8
 **/
public interface AuxTableInfoService {

    /**
     * 根据主键获取辅助表信息
     * @author lin.xc
     * @date 2021/04/8
     */
    AuxTableInfoDto getByPrimaryKey(String tableName);

    /**
     * 保证数据源表存在
     * @author lin.xc
     * @date 2021/04/8
     */
    void ensureDsTableExist(String tableName);

    /**
     * 查询辅助表数据
     * @author lin.xc
     * @date 2021-4-8
     * @param tableName 表名
     * @param selectColName 选择的列
     * @param condNameWithValue 条件名和值
     */
    List<Map> listAuxTableData(
            String tableName,
            List<String> selectColName,
            Map condNameWithValue
    );

    /**
     * 插入辅助表数据
     * @author lin.xc
     * @date 2021-4-9
     * @param tableName 表名
     * @param condNameWithValue 条件名和值
     */
    void insertAuxTableData(String tableName, Map condNameWithValue);

    /**
     * 插入辅助表数据 忽略异常（true：操作成功; false：操作异常）
     * @author lin.xc
     * @date 2021-4-9
     * @param tableName 表名
     * @param condNameWithValue 条件名和值
     */
    boolean insertAuxTableDataIgnoreException(String tableName, Map condNameWithValue);
}
