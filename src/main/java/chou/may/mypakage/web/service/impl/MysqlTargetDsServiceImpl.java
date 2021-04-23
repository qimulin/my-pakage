package chou.may.mypakage.web.service.impl;

import chou.may.mypakage.web.dao.MysqlTargetDsDalMapper;
import chou.may.mypakage.web.service.TargetDataSourceService;
import chou.may.mypakage.web.tailor.dto.TableColumnSimpleInfoDto;
import chou.may.mypakage.web.tailor.dto.TableSimpleInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author lin.xc
 * @date 2021/2/24
 **/
@Slf4j
@Service
public class MysqlTargetDsServiceImpl implements TargetDataSourceService {

    public static final String dbName = "business_data";
    @Autowired
    private MysqlTargetDsDalMapper dsDalMapper;

    @Override
    public List<TableSimpleInfoDto> listDsTable(String[] exceptTablePrefixArr, String tableName) {
        return dsDalMapper.listDataSourceTableInfo(dbName, exceptTablePrefixArr, tableName);
    }

    @Override
    public boolean checkTableExist(String tableName) {
        boolean flag = false;
        Integer count = dsDalMapper.countDataSourceTable(dbName, tableName);
        if(count>0){
            flag = true;
        }
        return flag;
    }

    @Override
    public List<TableColumnSimpleInfoDto> listDsTableColumn(String tableName) {
        return dsDalMapper.listDataSourceTableColumnInfo(dbName, tableName);
    }

    @Override
    public void createTableAsSelect(String tableName, String selectSql) {
        String tempTableName = "temp_".concat(tableName);
        log.debug("{}创建表来源查询sql：{}，临时表：{}", dbName, selectSql, tempTableName);
        dsDalMapper.createSelectByTemporaryTable(tableName,tempTableName,selectSql);
    }

    @Override
    public String copyTable(String sourceTableName, String targetTableName, boolean andData) {
        dsDalMapper.copyTableAndData(sourceTableName,targetTableName,andData);
        return targetTableName;
    }

    @Override
    public void dropTableIfExists(String tableName) {
        dsDalMapper.dropTableIfExists(tableName);
    }

    @Override
    public Long countData(String countSql) {
        return dsDalMapper.executeCountData(countSql);
    }

    @Override
    public List<Map> selectData(String selectSql) {
        return dsDalMapper.executeSelectData(selectSql);
    }

    @Override
    public List<Map> selectDataByCond(String tableName, List<String> selectColName, Map condColNameWithValue) {
        return dsDalMapper.selectDataByCond(tableName, selectColName, condColNameWithValue);
    }

    @Override
    public void executeDDL(String ddlSql) {
        dsDalMapper.executeDDL(ddlSql);
    }

    @Override
    public void insertData(String tableName, Map data) {
        dsDalMapper.insertData(tableName, data);
    }

    @Override
    public void insertDataList(String tableName, List<Map> dataList) {
        dsDalMapper.insertDataList(tableName, dataList);
    }

    @Override
    public void batchInsertDataList(String tableName, List<Map> dataList) {
        dsDalMapper.batchInsertDataList(tableName, dataList);
    }

}
