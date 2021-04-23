package chou.may.mypakage.web.dao;

import chou.may.mypakage.web.tailor.api.dto.TableColumnSimpleInfoDto;
import chou.may.mypakage.web.tailor.api.dto.TableSimpleInfoDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * MySql目标数据源服务
 * @author lin.xc
 * @date 2020/12/28
 **/
public interface MysqlTargetDsDalMapper {

    /**
     * 列举数据源表信息
     * @author lin.xc
     * @date 2021-2-24
     * */
    List<TableSimpleInfoDto> listDataSourceTableInfo(
            @Param("dbName") String dbName,
            @Param("exceptTablePrefixArr") String[] exceptTablePrefixArr,
            @Param("tableName") String tableName
    );

    /**
     * 计数数据源表
     * @author lin.xc
     * @date 2021-3-5
     * @param tableName 等于的表名
     * */
    Integer countDataSourceTable(
            @Param("dbName") String dbName,
            @Param("tableName") String tableName
    );

    /**
     * 列举数据源表信息
     * @author lin.xc
     * @date 2021-2-24
     * */
    List<TableColumnSimpleInfoDto> listDataSourceTableColumnInfo(
            @Param("dbName") String dbName,
            @Param("tableName") String tableName
    );

    /**
     * create sql语句 使用临时表
     * 【说明】这种操作可兼容RDS
     * @author lin.xc
     * */
    void createSelectByTemporaryTable(
            @Param("tableName") String tableName,
            @Param("tempTableName") String tempTableName,
            @Param("selectSql") String selectSql
    );

    /**
     * 拷贝表和数据
     * @author lin.xc
     * @date 2021-1-27
     * */
    void copyTableAndData(
            @Param("sourceTableName") String sourceTableName,
            @Param("targetTableName") String targetTableName,
            @Param("andData") boolean andData
    );

    /**
     * 删除表
     * @author lin.xc
     * @date 2021-1-27
     * */
    void dropTableIfExists(
            @Param("tableName") String tableName
    );

    /**
     * 计数表数据
     * 【注意】：由于有些数据源语法类似，为了复用，推荐用{@link #executeCountData(String)}
     * @author lin.xc
     * @date 2021-2-25
     * */
    @Deprecated
    Long countTableData(
            @Param("tableName") String tableName
    );

    /**
     * 分页查询表数据
     * 【注意】：由于有些数据源语法类似，为了复用，推荐用{@link #executeSelectData(String)}
     * @author lin.xc
     * @date 2021-2-26
     * */
    @Deprecated
    List<Map<String, Object>> pageTableData(
            @Param("tableName") String tableName,
            @Param("pageNo") Integer pageNo,
            @Param("pageSize") Integer pageSize
    );

    /**
     * 计数数据
     * @author lin.xc
     * @date 2021-2-26
     * */
    Long executeCountData(
            @Param("countSql") String countSql
    );

    /**
     * 查询数据
     * @author lin.xc
     * @date 2021-2-26
     * */
    List<Map> executeSelectData(
            @Param("selectSql") String selectSql
    );

    /**
     * @author lin.xc
     * @date 2021-4-1
     * */
    void executeDDL(
            @Param("ddlSql") String ddlSql
    );

    /**
     * 插入单条数据
     * @author lin.xc
     * @date 2021-4-13
     * */
    void insertData(
            String tableName,
            Map data
    );

    /**
     * 插入多条数据
     * @author lin.xc
     * @date 2021-4-12
     * */
    void insertDataList(
            String tableName,
            List<Map> dataList
    );

    /**
     * 批量插入数据
     * 要么都成功，要么都失败
     * 【注意】：字段格式统一
     * @author lin.xc
     * @date 2021-4-12
     * */
    void batchInsertDataList(
            String tableName,
            List<Map> dataList
    );

    /**
     * 根据条件查询数据
     * @param tableName 表名
     * @param selectColName 查询字段名称
     * @param condColNameWithValue 条件字段名称和条件值
     * @author lin.xc
     * @date 2020-02-26
     */
    List<Map> selectDataByCond(
            @Param("tableName") String tableName,
            @Param("selectColName") List<String> selectColName,
            @Param("condColNameWithValue") Map condColNameWithValue
    );
}
