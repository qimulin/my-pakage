# 定制API接口
## 涉及表结构脚本
辅助表
```sql
CREATE TABLE `aux_table_info` (
  `table_name` varchar(63) NOT NULL COMMENT '表名',
  `note` varchar(255) DEFAULT NULL COMMENT '注释',
  `create_script` text NOT NULL COMMENT '创建脚本',
  `is_need_cover` tinyint(1) NOT NULL COMMENT '是否需要覆盖',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`table_name`)
) COMMENT='辅助表信息';
```
定制接口表
```sql
CREATE TABLE `tailor_api` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(63) NOT NULL COMMENT '名称',
  `category` tinyint(2) NOT NULL COMMENT '分类',
  `note` varchar(255) DEFAULT NULL COMMENT '备注',
  `request_url` varchar(255) DEFAULT NULL COMMENT '请求地址（不包含参数）',
  `request_type` varchar(7) DEFAULT NULL COMMENT '请求类型',
  `res_resolver_path` varchar(255) NOT NULL COMMENT '响应解析器路径（类路径）',
  `version` int(11) NOT NULL COMMENT '版本',
  `is_enable` tinyint(1) NOT NULL COMMENT '是否可用',
  `is_use_aux_table` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否使用辅助表',
  `aux_table_name` varchar(127) DEFAULT NULL COMMENT '辅助表名',
  `create_time` datetime DEFAULT NULL COMMENT '插入时间',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) COMMENT='定制接口表';
```
定制接口参数表
```sql
CREATE TABLE `tailor_api_param` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `tailor_api_id` int(11) NOT NULL COMMENT '定制接口主键',
  `display_name` varchar(63) NOT NULL COMMENT '显示名',
  `name` varchar(63) NOT NULL COMMENT '参数名',
  `type` tinyint(1) NOT NULL COMMENT '参数类型',
  `is_leaf` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否为叶子',
  `parent_id` int(11) NOT NULL DEFAULT '-1' COMMENT '父参数主键（-1为无父参数）',
  `parse_type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '解析类型（1：元素；2：数组）',
  `is_required` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否必填',
  `is_enable` tinyint(1) NOT NULL COMMENT '是否可用',
  `ds_type` varchar(63) DEFAULT NULL COMMENT '数据源类型（输入参数可不设置）',
  `ds_length` varchar(63) DEFAULT NULL COMMENT '数据源长度（输入参数可不设置）',
  `create_time` datetime DEFAULT NULL COMMENT '插入时间',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) COMMENT='定制接口参数表';
```
## 示例服务调用
代码如下：
```java
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author lin.xc
 * @date 2021/4/1
 **/
@Slf4j
@Service
public class TailorApiNodeExeServiceImpl implements TailorApiNodeExeService {

    @Value("${tailorApi.ys.appKey}")
    private String ysAppKey;
    @Value("${tailorApi.ys.requestSecretRedisKey}")
    private String ysRequestSecretRedisKey;

    @Autowired
    private TailorApiService tailorApiService;
    @Autowired
    private NodeTailorApiParamService nodeTailorApiParamService;
    @Autowired
    private TargetDataSourceServiceProxy targetDataSourceServiceProxy;
    @Autowired
    private AuxTableInfoService auxTableInfoService;

    @Resource(name="defaultThreadPool")
    private ExecutorService defaultThreadPool;

    @Override
    public void addNodeTailorApiOutParamValueToTableCol(
            Integer nodeId,
            Integer tailorApiId,
            String nodeTableNameBuff,
            String nodeTableName
    ){
        log.info("***** 定制接口节点[id={},tableNameBuff={},tableName={}]添加输出参数值到表字段 *****", nodeId, nodeTableNameBuff, nodeTableName);
        // 查询节点输入参数
        List<NodeTailorApiParamWithColDto> inputParamList = listNodeTailorApiLeafParamWithCol(nodeId, tailorApiId, TailorApiParamTypeEnum.INPUT);
        if(CollectionUtils.isEmpty(inputParamList)){
            throw new RunTimeExceptionByDefine(ResponseObject.fail("不支持该组件没有任何参数匹配输入，nodeId="+nodeId));
        }

        // 构建下面两个Map方便构建请求和响应
        Map<String, Integer> colNameWithInputParamIdMap = getColNameWithInputParamIdMap(nodeId, tailorApiId);
        log.info("表字段名与接口输入参数id关系映射为：{}", colNameWithInputParamIdMap);
        Map<Integer, String> outputParamIdWithColNameMap = getOutputParamIdWithColNameMap(nodeId, tailorApiId);
        log.info("接口输出参数id与表字段名关系映射为：{}", outputParamIdWithColNameMap);

        // 查询该接口所有输入参数
        List<TailorApiParamDto> inParamList
                = tailorApiService.listTailorApiParam(tailorApiId, TailorApiParamTypeEnum.INPUT.getId(), null);
        Set<TailorApiParamTreeNode> inParamTreeNodes = toTailorApiParamTreeNodeSet(inParamList);
        log.info("输入参数格式树为：{}",JSONObject.toJSONString(inParamTreeNodes));
        // 查询该接口所有输出参数
        List<TailorApiParamDto> outParamList
                = tailorApiService.listTailorApiParam(tailorApiId, TailorApiParamTypeEnum.OUTPUT.getId(), null);
        Set<TailorApiParamTreeNode> outParamTreeNodes = toTailorApiParamTreeNodeSet(
                filterUsefulTailorApiOutParam(outParamList, outputParamIdWithColNameMap.keySet()));
        log.info("输出参数格式树为：{}",JSONObject.toJSONString(outParamTreeNodes));

        // 先查询表数据总量
        Long count = targetDataSourceServiceProxy.countDataByTableName(nodeTableNameBuff);
        if(count==0){
            log.info("节点[id={},tableName={}]没查到数据，无需任何处理，直接返回");
            return;
        }

        // 获取定制接口处理器
        TailorApiDto tailorApi = tailorApiService.getTailorApi(tailorApiId);
        log.info("该节点选择的定制接口信息为：{}", JSONObject.toJSONString(tailorApi));
        // 获取接口入参构建器
        TailorApiInParamBuilder tailorApiInParamBuilder = new TailorApiInParamBuilder(colNameWithInputParamIdMap, inParamTreeNodes);
        // 获取接口返回解析器
        TailorApiResResolver tailorApiResResolver = getTailorApiResResolver(tailorApi, outParamTreeNodes, outputParamIdWithColNameMap);
        // 获取定制接口处理器
        AbsTailorApiHandler tailorApiHandler = getTailorApiHandler(tailorApi, tailorApiResResolver);

        // 需要获取处理入参的字段名集合
        Set<String> inputParamColNames = inputParamList.stream().map(NodeTailorApiParamWithColDto::getColumnAliasName).collect(Collectors.toSet());

        // 设置分批需要的一些参数初始值
        int maxQueryNum = 0, pageSize = TailorApiNodeExeTask.BATCH_CAPACITY, pageNum = 1;
        try {
            // 预计最大查询条数小于数据数量时，执行逻辑
            while(maxQueryNum<count){
                // 查询单批次数据
                List<Map> fromDataList = targetDataSourceServiceProxy.pageDataByTableName(nodeTableNameBuff, pageNum, pageSize);

                maxQueryNum = pageNum * pageSize;
                log.info("定制接口节点[id={}]查询数据[pageNum={},pageSize={}]加入定制接口组件执行任务处理", nodeId, pageNum, pageSize);
                // 加入任务
                TailorApiNodeExeTask task = new TailorApiNodeExeTask(
                        fromDataList, inputParamColNames, tailorApiHandler, tailorApiInParamBuilder, nodeTableName, targetDataSourceServiceProxy
                );
                Future<?> future = defaultThreadPool.submit(task);
                // 若有异常，则抛出异常
                future.get();
                // 计算分页相关参数
                pageNum++;
            }
        }catch (Exception e){
            log.error("多线程定制接口组件任务执行出错！错误：{}",e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("多线程定制接口组件任务执行出错！错误："+e.getMessage());
        }
    }

    /******************************************* 辅助方法 *********************************************/
    /**
     * 获取节点叶子参数列和字段关系
     * @author lin.xc
     * @date 2021-4-1
     * */
    private List<NodeTailorApiParamWithColDto> listNodeTailorApiLeafParamWithCol(
            Integer nodeId,
            Integer tailorApiId,
            TailorApiParamTypeEnum typeEnum
    ){
        ListNodeTailorApiParamWithColCondition condition = new ListNodeTailorApiParamWithColCondition();
        condition.setNodeId(nodeId);
        condition.setTailorApiId(tailorApiId);
        condition.setIsLeaf(CommonIsEnum.YES.getTinyCode());
        condition.setIsDisplay(CommonIsEnum.YES.getTinyCode());
        condition.setType(typeEnum.getId());
        return nodeTailorApiParamService.listNodeTailorApiParamWithCol(condition);
    }

    /**
     * 获取<列名,输入参数id>关系
     * @author lin.xc
     * @date 2021-4-1
     * */
    private Map<String, Integer>  getColNameWithInputParamIdMap(
            Integer nodeId,
            Integer tailorApiId
    ){
        // 查询节点输入参数
        List<NodeTailorApiParamWithColDto> inputParamList = listNodeTailorApiLeafParamWithCol(nodeId, tailorApiId, TailorApiParamTypeEnum.INPUT);
        if(CollectionUtils.isEmpty(inputParamList)){
            throw new RunTimeExceptionByDefine(ResponseObject.fail("不支持该组件没有任何参数匹配输入，nodeId="+nodeId));
        }

        // 构建Map<columnAliasName, tailorApiParamId> 方便匹配
        Map<String, Integer> colNameWithParamIdMap = inputParamList.stream().collect(
                Collectors.toMap(NodeTailorApiParamWithColDto::getColumnAliasName, NodeTailorApiParamWithColDto::getTailorApiParamId));
        return colNameWithParamIdMap;
    }

    /**
     * 获取<输出参数id,列名>关系
     * @author lin.xc
     * @date 2021-4-1
     * */
    private Map<Integer, String>  getOutputParamIdWithColNameMap(
            Integer nodeId,
            Integer tailorApiId
    ){
        Map<Integer, String> colNameWithParamIdMap = new HashMap<>();
        // 查询节点输入参数
        List<NodeTailorApiParamWithColDto> outParamList = listNodeTailorApiLeafParamWithCol(nodeId, tailorApiId, TailorApiParamTypeEnum.OUTPUT);
        if(CollectionUtils.isNotEmpty(outParamList)){
            colNameWithParamIdMap = outParamList.stream().collect(
                    Collectors.toMap(NodeTailorApiParamWithColDto::getTailorApiParamId, NodeTailorApiParamWithColDto::getColumnAliasName));
        }
        return colNameWithParamIdMap;
    }

    /**
     * list转树形结构
     * @author lin.xc
     * @date 2021-4-1
     * */
    private static Set<TailorApiParamTreeNode> toTailorApiParamTreeNodeSet(
            List<TailorApiParamDto> paramList
    ){
        ToOtherObjectFunction<TailorApiParamDto, TailorApiParamTreeNode> func = (p, args)->{
            TailorApiParamTreeNode r = new TailorApiParamTreeNode(
                    p.getId(),p.getName(),p.getIsLeaf(),p.getParseType(),p.getParentId()
            );
            return r;
        };
        // 转树类
        List<TailorApiParamTreeNode> treeList = func.castList(paramList);
        // 转Map，辅助转成树
        Map<Integer, TailorApiParamTreeNode> map
                = treeList.stream().collect(Collectors.toMap(TailorApiParamTreeNode::getId, Function.identity()));
        // 定义结果对象
        Set<TailorApiParamTreeNode> result = new HashSet<>();
        for (TailorApiParamTreeNode idx : treeList) {
            TailorApiParamTreeNode parent = map.get(idx.getParentId());
            if(parent!=null){
                parent.addChild(idx);
            }else{
                //没有父级节点
                result.add(idx);
            }
        }
        return result;
    }

    /**
     * 过滤出有用的输出参数
     * @author lin.xc
     * @date 2021-4-1
     * */
    private List<TailorApiParamDto> filterUsefulTailorApiOutParam(
            List<TailorApiParamDto> paramList,
            Set<Integer> useIdSet
    ){
        List<TailorApiParamDto> result = new ArrayList<>();
        for(TailorApiParamDto apiParamDto: paramList){
            // 非叶子节点或者使用到叶子节点才去做树形转换
            if(apiParamDto.getIsLeaf()==CommonIsEnum.NO.getTinyCode() ||
                    (apiParamDto.getIsLeaf()==CommonIsEnum.YES.getTinyCode() && useIdSet.contains(apiParamDto.getId()))){
                result.add(apiParamDto);
            }
        }
        return result;
    }

    /**
     * 转ColumnSqlDto
     * @author lin.xc
     * @date 2021-4-1
     * */
    private ToOtherObjectFunction<NodeTailorApiParamWithColDto, ColumnSqlDto> toColumnSqlDtoFunc(){
        ToOtherObjectFunction<NodeTailorApiParamWithColDto, ColumnSqlDto> func = (p, args)->{
            ColumnSqlDto r = new ColumnSqlDto();
            r.setName(p.getColumnAliasName());
            return r;
        };
        return func;
    }

    /**
     * 获取定制接口返回解析器
     * @author lin.xc
     * @date 2021-4-15
     * */
    private TailorApiResResolver getTailorApiResResolver(
            TailorApiDto tailorApi,
            Set<TailorApiParamTreeNode> outParamTreeNodes,
            Map<Integer, String> outParamIdWithColNameMap
    ){
        AbsTailorApiResResolver tailorApiResResolver = newTailorApiResResolverInstance(tailorApi.getResResolverPath());
        tailorApiResResolver.setTailorApiOutParamResolver(outParamTreeNodes, outParamIdWithColNameMap);
        return tailorApiResResolver;
    }

    /**
     * 获取定制接口处理器
     * @author lin.xc
     * @date 2021-4-1
     * */
    private AbsTailorApiHandler getTailorApiHandler(
            TailorApiDto tailorApi,
            TailorApiResResolver tailorApiResResolver
    ){
        TailorApiCategoryEnum categoryEnum = TailorApiCategoryEnum.of(tailorApi.getCategory());
        log.info("获取接口处理器类别为：{}", categoryEnum.getDesc());
        switch (categoryEnum){
            case YS:
                YsApiHandler ysApiHandler = new YsApiHandler(tailorApi.getRequestUrl(), tailorApiResResolver);
                ysApiHandler.setAppKey(ysAppKey);
                ysApiHandler.setRequestSecretRedisKey(ysRequestSecretRedisKey);
                // 若有辅助表，并设置使用。则要保证其存在，并设置
                String auxTableName = tailorApi.getAuxTableName();
                if(StringUtils.isNotBlank(auxTableName)
                        && CommonIsEnum.YES.getTinyCode()==tailorApi.getIsUseAuxTable()){
                    auxTableInfoService.ensureDsTableExist(auxTableName);
                    ysApiHandler.setAuxTableName(auxTableName);
                    ysApiHandler.setAuxTableInfoService(auxTableInfoService);
                }
                return ysApiHandler;
            default:
                throw new IllegalArgumentException("暂无法获取类型为"+categoryEnum.name()+"的定制接口处理器！");
        }
    }

    /**
     * 实例化接口解析器
     * @author lin.xc
     * @date 2021-4-1
     * */
    private static AbsTailorApiResResolver newTailorApiResResolverInstance(
            String clazzPath
    ){
        log.info("实例化 接口解析器。路径：{}", clazzPath);
        try {
            Class clazz  =Class.forName(clazzPath);
            AbsTailorApiResResolver instance = (AbsTailorApiResResolver) clazz.newInstance();
            return instance;
        } catch (Exception e) {
            throw new RunTimeExceptionByDefine(ResponseObject.fail("实例化 接口解析器出错！错误信息："+e.toString()));
        }
    }

}
```


