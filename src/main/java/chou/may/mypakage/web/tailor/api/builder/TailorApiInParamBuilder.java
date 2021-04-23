package chou.may.mypakage.web.tailor.api.builder;

import chou.may.mypakage.web.constant.CommonIsEnum;
import chou.may.mypakage.web.tailor.api.TailorApiParamTreeNode;
import com.alibaba.fastjson.JSONObject;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 定制接口输入参数构建器
 * @author lin.xc
 * @date 2021/4/14
 **/
public class TailorApiInParamBuilder {

    /** 表字段名和输入参数id关系，方便构建请求 */
    protected Map<String, Integer> colNameWithInParamIdMap;
    /** 输入参数树形格式，方便格式构建 */
    protected Set<TailorApiParamTreeNode> inParamTreeNodes;

    public TailorApiInParamBuilder(
            Map<String, Integer> colNameWithInParamIdMap,
            Set<TailorApiParamTreeNode> inParamTreeNodes
    ){
        Assert.notNull(colNameWithInParamIdMap, "colNameWithInParamIdMap不能设置为空");
        Assert.notNull(inParamTreeNodes, "inParamTreeNodes不能设置为空");
        this.colNameWithInParamIdMap = colNameWithInParamIdMap;
        this.inParamTreeNodes = inParamTreeNodes;
    }

    /**
     * 构建输入参数的参数名和参数字符串
     * @param colNameWithValueMap 字段名和值的Map
     * */
    public Map<String, String> buildInputParamNameWithValueStr(
            Map colNameWithValueMap
    ){
        return buildTailorApiInParams(buildParamIdWithValueMap(colNameWithValueMap));
    }


    /**
     * 构建参数id和参数值Map
     * @param colNameWithValueMap 字段名和值的Map
     * */
    public Map<Integer, Object> buildParamIdWithValueMap(
            Map colNameWithValueMap
    ){
        // 构建Map<tailorApiParamId, value>
        Map<Integer, Object> paramIdWithValueMap = new HashMap<>();
        for(Object key: colNameWithValueMap.keySet()){
            // 获取该字段对应的tailorApiParamId
            Integer paramId = this.colNameWithInParamIdMap.get(key);
            paramIdWithValueMap.put(paramId, colNameWithValueMap.get(key));
        }
        return paramIdWithValueMap;
    }

    /**
     * 构建输入参数
     * @param paramIdWithValueMap <tailorApiParamId,value>
     * */
    public Map<String, String> buildTailorApiInParams(
            Map<Integer, Object> paramIdWithValueMap
    ){
        Map<String, String> result = new HashMap<>();
        // 只需处理Set最外层的节点
        for(TailorApiParamTreeNode node: this.inParamTreeNodes){
            String valueStr;
            Object value = buildTailorApiInParam(node, paramIdWithValueMap);
            if(value instanceof JSONObject){
                // 处理分层的情况
                valueStr = JSONObject.toJSONString(value);
            }else{
                valueStr = value==null?"":value.toString();
            }
            result.put(node.getName(), valueStr);
        }
        return result;
    }

    /**
     * 构建请求参数
     * */
    private static Object buildTailorApiInParam(
            TailorApiParamTreeNode node,
            Map<Integer, Object> paramIdWithValueMap
    ){
        // 叶子节点，直接取值
        if(CommonIsEnum.YES.getTinyCode()==node.getIsLeaf()){
            Integer tailorApiParamId = node.getId();
            return paramIdWithValueMap.get(tailorApiParamId);
        }else{
            JSONObject currNodeJsonObj = new JSONObject();
            List<TailorApiParamTreeNode> childrenNodes = node.getChildrenNodes();
            for(TailorApiParamTreeNode childNode: childrenNodes){
                Object childParam = buildTailorApiInParam(childNode, paramIdWithValueMap);
                currNodeJsonObj.put(childNode.getName(), childParam);
            }
            return currNodeJsonObj;
        }
    }

}
