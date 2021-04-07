package chou.may.mypakage.web.tailor;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 定制接口参数处理
 * @author lin.xc
 * @date 2021/3/31
 **/
public class TailorApiParamHandler {

    public static final byte IS_LEAF_YES = 1;

    /**
     * 构建输入参数
     * @param paramIdWithValueMap <tailorApiParamId,value>
     * */
    public static Map<String, String> buildTailorApiInParams(
            Set<TailorApiParamTreeNode> paramTree,
            Map<Integer, Object> paramIdWithValueMap
    ){
        Map<String, String> result = new HashMap<>();
        // 只需处理Set最外层的节点
        for(TailorApiParamTreeNode node: paramTree){
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
        if(IS_LEAF_YES==node.getIsLeaf()){
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

    /**
     * 解析API输出参数
     * @param resultJsonStr 结果JSON字符串
     * @param paramTree 参数树
     * */
    public static Map<Integer, Object> resolveTailorApiOutParams(
            String resultJsonStr,
            Set<TailorApiParamTreeNode> paramTree
    ){
        JSONObject resultJson = JSONObject.parseObject(resultJsonStr);
        return resolveTailorApiOutParams(resultJson, paramTree);
    }

    /**
     * 解析API输出参数
     * @param resultJson 结果JSON
     * @param paramTree 参数树
     * */
    public static Map<Integer, Object> resolveTailorApiOutParams(
            JSONObject resultJson,
            Set<TailorApiParamTreeNode> paramTree
    ){
        Map<Integer, Object> paramIdWithValueMap = new HashMap<>();
        for(TailorApiParamTreeNode node: paramTree){
            resolveTailorApiOutParam(resultJson, node, paramIdWithValueMap);
        }
        return paramIdWithValueMap;
    }

    /**
     * 【递归】解析响应结果
     * */
    private static void resolveTailorApiOutParam(
            JSONObject outJsonObj,
            TailorApiParamTreeNode node,
            Map<Integer, Object> paramIdWithValueMap
    ){
        // 叶子节点直接取值
        if(IS_LEAF_YES==node.getIsLeaf()){
            // TODO：嫌麻烦，先都转成字符串
            String outParamValue = outJsonObj.getString(node.getName());
            paramIdWithValueMap.put(node.getId(), outParamValue);
        }else{
            // 获取本节点的Json对象
            JSONObject currNodeJsonObj = outJsonObj.getJSONObject(node.getName());
            List<TailorApiParamTreeNode> childrenNodes = node.getChildrenNodes();
            for(TailorApiParamTreeNode childNode: childrenNodes){
                resolveTailorApiOutParam(currNodeJsonObj, childNode, paramIdWithValueMap);
            }
        }
    }

}
