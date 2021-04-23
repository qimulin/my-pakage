package chou.may.mypakage.web.service;

import chou.may.mypakage.web.tailor.AbsTailorApiHandler;
import chou.may.mypakage.web.tailor.builder.TailorApiInParamBuilder;
import com.alibaba.fastjson.JSONArray;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * @author lin.xc
 * @date 2021/4/6
 **/
@Slf4j
public class TailorApiNodeExeTask implements Runnable {

    /** 处理批次容量 */
    public static final Integer BATCH_CAPACITY = 100;
    /** 需要处理的来源数据 */
    private List<Map> fromDataList;
    /** 入参字段名称集 */
    private Set<String> inputParamColNames;
    /** 定制API处理器 */
    private AbsTailorApiHandler tailorApiHandler;
    /** 定制API输入参数处理器 */
    private TailorApiInParamBuilder tailorApiInParamBuilder;
    /** 节点表名 */
    private String nodeTableName;
    /** 目标数据源服务代理 */
    private TargetDataSourceService targetDataSourceServiceProxy;

    /**
     * 构造函数传入
     * */
    public TailorApiNodeExeTask(
            List<Map> fromDataList,
            Set<String> inputParamColNames,
            AbsTailorApiHandler tailorApiHandler,
            TailorApiInParamBuilder tailorApiInParamBuilder,
            String nodeTableName,
            TargetDataSourceService targetDataSourceServiceProxy
    ) {
        int size = fromDataList.size();
        if(size>BATCH_CAPACITY){
            // 如果大于容量，则抛异常
            throw new RuntimeException("定制接口节点运行，任务参数list超容量大小"+BATCH_CAPACITY);
        }
        this.fromDataList = fromDataList;
        this.inputParamColNames = inputParamColNames;
        this.tailorApiHandler = tailorApiHandler;
        this.tailorApiInParamBuilder = tailorApiInParamBuilder;
        this.nodeTableName = nodeTableName;
        this.targetDataSourceServiceProxy = targetDataSourceServiceProxy;
    }

    @Override
    public void run() {
        // 定义批量插入数据
        List<Map> batchInsertDataList = new ArrayList<>();
        // 逐个Map获取入参值
        for(Map fromData: this.fromDataList){
            // 构建入参Map
            Map inParamNameWithValueMap = buildInParamNameWithValueMap(fromData, this.inputParamColNames);
            // 单个请求并返回输出数据
            List<Map<String, Object>> colNameWithOutParamValueList = requestAndReturnOutDataList(inParamNameWithValueMap);
            log.debug("请求后表字段名对应输出结果值为：{}", JSONArray.toJSONString(colNameWithOutParamValueList));
            // 输出不为空，则与入参匹配
            if(CollectionUtils.isNotEmpty(colNameWithOutParamValueList)){
                // 字段名和输出参数值
                for (Map colNameWithOutParamValue: colNameWithOutParamValueList){
                    // 为每条输出都添加上来源数据
                    colNameWithOutParamValue.putAll(fromData);
                    // 添加到批次数据中
                    batchInsertDataList.add(colNameWithOutParamValue);
                }
            }else{
                // 没有匹配到输出数据，则插入原始数据
                batchInsertDataList.add(fromData);
            }
        }
        // 执行修改
        targetDataSourceServiceProxy.insertDataList(this.nodeTableName, batchInsertDataList);
    }

    /**
     * 请求并返回<表字段名，输出参数值>的输出数据
     * @author lin.xc
     * @date 2021-4-1
     * @param inParamNameWithValueMap 入参字表段名和值的关系，方便构建请求
     * @return Map<String, Object> 字段名和输出参数值Map
     * */
    private List<Map<String, Object>> requestAndReturnOutDataList(
            Map inParamNameWithValueMap
    ) {
        List<Map<String, Object>> outDataList = new ArrayList<>();
        // 构建请求
        Map<String, String> reqParamMap = this.tailorApiInParamBuilder.buildInputParamNameWithValueStr(inParamNameWithValueMap);
        String resData = this.tailorApiHandler.requestAndReturnDataStr(reqParamMap);
        // 仅当返回数据不为空才处理
        if(StringUtils.isNotEmpty(resData)){
            outDataList = this.tailorApiHandler.getTailorApiResResolver().getColNameWithOutParamValue(resData);
        }
        return outDataList;
    }

    /**
     * 构建入参名和值的关系，只需要能拿来做入参的
     * @author lin.xc
     * @date 2021-4-13
     * */
    private static Map<String, Object> buildInParamNameWithValueMap(Map<String, Object> mapData, Set<String> choiceNames){
        Map<String, Object> result = new HashMap<>();
        for (String cName: choiceNames){
            if(mapData.containsKey(cName)){
                result.put(cName, mapData.get(cName));
            }
        }
        return result;
    }
}
