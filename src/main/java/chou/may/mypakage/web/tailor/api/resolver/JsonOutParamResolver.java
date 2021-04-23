package chou.may.mypakage.web.tailor.api.resolver;

import chou.may.mypakage.web.tailor.api.TailorApiParamTreeNode;
import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * JSON类型的参数处理
 * @author lin.xc
 * @date 2021/4/14
 **/
public class JsonOutParamResolver extends AbsTailorApiOutParamResolver<JSONObject> {

    /**
     * 构造函数
     * */
    public JsonOutParamResolver(
            Set<TailorApiParamTreeNode> outParamTreeNodes,
            Map<Integer, String> outParamIdWithColNameMap
    ) {
        super(outParamTreeNodes, outParamIdWithColNameMap);
    }

    @Override
    protected JSONObject parseStrToElement(String elementStr) {
        return JSONObject.parseObject(elementStr);
    }

    @Override
    protected List<JSONObject> parseStrToElements(String elementsStr) {
        return JSONObject.parseArray(elementsStr, JSONObject.class);
    }

    @Override
    protected Object getLeafChildNodeValue(JSONObject currElement, String childNodeName) {
        return currElement.get(childNodeName);
    }

    @Override
    protected JSONObject getElementChildNodeValue(JSONObject currElement, String childNodeName) {
        return currElement.getJSONObject(childNodeName);
    }

    @Override
    protected List<JSONObject> getElementsChildNodeValue(JSONObject currElement, String childNodeName) {
        return parseStrToElements(currElement.getString(childNodeName));
    }

}
