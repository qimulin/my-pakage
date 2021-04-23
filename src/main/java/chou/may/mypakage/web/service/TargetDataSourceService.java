package chou.may.mypakage.web.service;

import chou.may.mypakage.web.tailor.api.dto.TableColumnSimpleInfoDto;
import chou.may.mypakage.web.tailor.api.dto.TableSimpleInfoDto;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Map;

/**
 * 目前本系统只支持一个目标数据源，
 * 若要同时支持多个目标源，整个系统代码要配合改动
 * TODO：本例子需要去实现
 * @author lin.xc
 * @date 2021/2/24
 **/
public interface TargetDataSourceService {

    /**
     * 查询数据源表格信息
     * @author lin.xc
     * @date 2021/2/24
     * @param exceptTablePrefixArr 排除表前缀数组
     * @param tableName 查询的表名 模糊匹配
     * */
    List<TableSimpleInfoDto> listDsTable(String[] exceptTablePrefixArr, String tableName);

    /**
     * 检查表是否存在
     * @author lin.xc
     * @date 2021/3/5
     * @param tableName 查询的表名
     * */
    boolean checkTableExist(String tableName);

    /**
     * 查询数据源表格字段信息
     * @author lin.xc
     * @date 2021/2/24
     * @param tableName 查询的表名
     * */
    List<TableColumnSimpleInfoDto> listDsTableColumn(String tableName);

    /**
     * 根据查询语句创建表
     * @author lin.xc
     * @date 2020-12-28
     * @param tableName 中间表名
     * @param selectSql 查询Sql
     * @return
     */
    void createTableAsSelect(String tableName, String selectSql);

    /**
     * 拷贝表
     * @author lin.xc
     * @date 2020-01-20
     * @param sourceTableName 源表名
     * @param targetTableName 目标表名
     * @param andData 是否包括数据
     * @return
     */
    String copyTable(
            String sourceTableName,
            String targetTableName,
            boolean andData
    );

    /**
     * 删除表
     * @author lin.xc
     * @date 2020-02-25
     * */
    void dropTableIfExists(String tableName);

    /**
     * 计数查询数据
     * @author lin.xc
     * @date 2020-02-26
     */
    Long countData(String countSql);

    /**
     * 查询数据
     * @author lin.xc
     * @date 2020-02-26
     */
    List<Map> selectData(
            String selectSql
    );

    /**
     * 分页查询数据
     * @param tableName 表名
     * @param selectColName 查询字段名称
     * @param condColNameWithValue 条件字段名称和条件值
     * @author lin.xc
     * @date 2020-02-26
     */
    List<Map> selectDataByCond(
            String tableName,
            @Nullable
                    List<String> selectColName,
            Map condColNameWithValue
    );

    /**
     * 执行数据源的DDL（Data Definition Language 数据定义语言）语句
     * @author lin.xc
     * @date 2021-04-01
     * */
    void executeDDL(
            String ddlSql
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
     * @date 2021-4-22
     * */
    void insertDataList(
            String tableName,
            List<Map> dataList
    );

    /**
     * 批量插入数据
     * @author lin.xc
     * @date 2021-4-12
     * */
    void batchInsertDataList(
            String tableName,
            List<Map> dataList
    );
}
