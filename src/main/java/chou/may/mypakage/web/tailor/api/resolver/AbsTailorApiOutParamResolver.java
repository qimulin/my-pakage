package chou.may.mypakage.web.tailor.api.resolver;

import chou.may.mypakage.web.constant.CommonIsEnum;
import chou.may.mypakage.web.tailor.api.ParamParseTypeEnum;
import chou.may.mypakage.web.tailor.api.TailorApiParamTreeNode;
import chou.may.mypakage.web.util.MapUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.Assert;

import java.util.*;

/**
 * 有数接口输出参数辅助器
 * @author lin.xc
 * @date 2021/4/14
 **/
public abstract class AbsTailorApiOutParamResolver<E> implements TailorApiOutParamResolver{

    /** 输出参数树形格式，方便格式解析 */
    protected Set<TailorApiParamTreeNode> outParamTreeNodes;
    /** 输出参数id和表字段名的关系，方便返回请求 */
    protected Map<Integer, String> outParamIdWithColNameMap;

    public AbsTailorApiOutParamResolver(
            Set<TailorApiParamTreeNode> outParamTreeNodes,
            Map<Integer, String> outParamIdWithColNameMap
    ){
        Assert.notNull(outParamTreeNodes, "outParamTreeNodes不能设置为空");
        Assert.notNull(outParamIdWithColNameMap, "outParamIdWithColNameMap不能设置为空");
        this.outParamTreeNodes = outParamTreeNodes;
        this.outParamIdWithColNameMap = outParamIdWithColNameMap;
    }

    @Override
    public List<Map<String, Object>> getColNameWithOutParamValue(String resDataStr){
        return buildColNameWithOutParamValue(getParamIdWithValue(resDataStr));
    }

    @Override
    public List<Map<String, Object>> getColNameWithOutParamValueForArr(String resDataStr){
        return buildColNameWithOutParamValue(getParamIdWithValueForArr(resDataStr));
    }

    /**
     * 获取参数id和值
     * @param resDataStr 返回数据字符串
     * */
    protected List<Map<Integer, Object>> getParamIdWithValue(String resDataStr){
        return resolveElementOutParam(parseStrToElement(resDataStr), this.outParamTreeNodes);
    }

    /**
     * 解析字符串到单个元素
     * @param elementStr 元素字符串
     * */
    protected abstract E parseStrToElement(String elementStr);

    /**
     * 获取参数id和值（数组返回）
     * @param resDataStr 返回数据字符串
     * */
    protected List<Map<Integer, Object>> getParamIdWithValueForArr(String resDataStr){
        List<Map<Integer, Object>> resultList = new ArrayList<>();
        List<E> list = parseStrToElements(resDataStr);
        for (int i = 0; i < list.size(); i++) {
            // 添加每次处理的结果
            resultList.addAll(resolveElementOutParam(list.get(i), this.outParamTreeNodes));
        }
        return resultList;
    }

    /**
     * 解析字符串到元素集合
     * @param elementsStr 元素集合字符串
     * */
    protected abstract List<E> parseStrToElements(String elementsStr);

    /**
     * 解析单元素开始的输出参数
     * 【递归函数】：欢迎优化
     * @author lin.xc
     * @date 2021-4-16
     * */
    protected List<Map<Integer, Object>> resolveElementOutParam(
            E currElement,
            Collection<TailorApiParamTreeNode> childrenNodes
    ){
        // 定义本层和子层的参数id和值
        List<Map<Integer, Object>> currAndSubParamIdWithValueList = new ArrayList<>();
        // 若没有子属性，则直接返回
        if(CollectionUtils.isEmpty(childrenNodes)){
            return currAndSubParamIdWithValueList;
        }
        // 接收非叶子节点，后面统一处理
        List<TailorApiParamTreeNode> notLeafChildNodes = new ArrayList<>();
        // 叶子参数id和值
        Map leafParamIdWithValueMap = new HashMap<>();
        for(TailorApiParamTreeNode childNode: childrenNodes){
            // 叶子节点先作为属性处理
            if(CommonIsEnum.YES.getTinyCode()==childNode.getIsLeaf()){
                // TODO：嫌麻烦，都转成字符串
                Object outParamValue = getLeafChildNodeValue(currElement, childNode.getName());
                leafParamIdWithValueMap.put(childNode.getId(), outParamValue);
            }else{
                // 非叶子节点，先记录，后面统一处理
                notLeafChildNodes.add(childNode);
            }
        }
        // 将叶子节点添加到本层的关系数据中
        MapUtils.putAllToMapListElement(currAndSubParamIdWithValueList,leafParamIdWithValueMap);
        // 遍历非叶子节点
        for(TailorApiParamTreeNode childNode: notLeafChildNodes){
            // 非叶子节点，判断解析类型
            ParamParseTypeEnum childParseType = ParamParseTypeEnum.of(childNode.getParseType());
            switch (childParseType){
                case ELEMENT:
                    // 获取子元素JSON对象
                    E childElement = getElementChildNodeValue(currElement, childNode.getName());
                    // 下一层的数据
                    List<Map<Integer, Object>> childParamIdWithValueList = resolveElementOutParam(childElement, childNode.getChildrenNodes());
                    // 笛卡尔积下层数据
                    currAndSubParamIdWithValueList = MapUtils.cartesianMapList(currAndSubParamIdWithValueList, childParamIdWithValueList);
                    break;
                case ARRAY:
                    // 获取子元素JSON对象
                    List<E> childElementList = getElementsChildNodeValue(currElement, childNode.getName());
                    // 子层数据集合
                    List<Map<Integer, Object>> subParamIdWithValueList = new ArrayList<>();
                    for (int i = 0; i < childElementList.size(); i++) {
                        // 逐一添加
                        subParamIdWithValueList.addAll(resolveElementOutParam(childElementList.get(i), childNode.getChildrenNodes()));
                    }
                    // 笛卡尔积下层数据
                    currAndSubParamIdWithValueList = MapUtils.cartesianMapList(currAndSubParamIdWithValueList, subParamIdWithValueList);
                    break;
                default:
                    throw new IllegalArgumentException("暂不支持对该解析类型的处理，类型="+childParseType.name());
            }
        }
        return currAndSubParamIdWithValueList;
    }

    /**
     * 获取单个叶子子节点值
     * @param currElement 当前元素
     * @param childNodeName 子节点名称
     * */
    protected abstract Object getLeafChildNodeValue(E currElement, String childNodeName);

    /**
     * 获取单个元素子节点值
     * @param currElement 当前元素
     * @param childNodeName 子节点名称
     * */
    protected abstract E getElementChildNodeValue(E currElement, String childNodeName);

    /**
     * 获取元素子节点集合值
     * @param currElement 当前元素
     * @param childNodeName 子节点名称
     * */
    protected abstract List<E> getElementsChildNodeValue(E currElement, String childNodeName);

    /**
     * 构建列名和输出参数值关系
     * 根据传入的关系映射，将值再与表字段匹配上
     * */
    private List<Map<String, Object>> buildColNameWithOutParamValue(List<Map<Integer, Object>> outParamIdWithValueList){
        // 定义结果
        List<Map<String, Object>> resultList = new ArrayList<>();
        for(Map<Integer, Object> outParamIdWithValue: outParamIdWithValueList){
            Map<String, Object> colNameWithOutParamValue = new HashMap<>();
            // 遍历结果
            for(Integer tailorParamId: this.outParamIdWithColNameMap.keySet()){
                // 只取我需要的值
                colNameWithOutParamValue.put(this.outParamIdWithColNameMap.get(tailorParamId),outParamIdWithValue.get(tailorParamId));
            }
            resultList.add(colNameWithOutParamValue);
        }
        return resultList;
    }
}
