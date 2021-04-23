package chou.may.mypakage.web.tailor.resolver;

import chou.may.mypakage.web.tailor.DataTypeEnum;
import chou.may.mypakage.web.tailor.TailorApiParamTreeNode;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author lin.xc
 * @date 2021/4/14
 **/
public abstract class AbsTailorApiResResolver implements TailorApiResResolver{

    /** 数超出参数解析器 */
    protected AbsTailorApiOutParamResolver outParamResolver;

    public void setTailorApiOutParamResolver(
            Set<TailorApiParamTreeNode> outParamTreeNodes,
            Map<Integer, String> outParamIdWithColNameMap
    ){
        DataTypeEnum resDataType = getResDataType();
        switch (resDataType){
            case JSON:
                outParamResolver = new JsonOutParamResolver(outParamTreeNodes, outParamIdWithColNameMap);
                break;
            case XML:
                outParamResolver = new XmlOutParamResolver(outParamTreeNodes, outParamIdWithColNameMap);
                break;
            default:
                throw new RuntimeException("暂不支持对该数据类型字符串的处理！type="+resDataType.name());
        }
    }

    @Override
    public List<Map<String, Object>> getColNameWithOutParamValue(String resDataStr) {
        Assert.notNull(this.outParamResolver, "需要事先设置接口输出参数处理器！");
        return doResolveResForOutColNameAndValue(resDataStr);
    }

    /**
     * 子类可以根据具体的情况重写该方法进行解析
     * 主要是分单元素还是数组的情况
     * */
    protected List<Map<String, Object>> doResolveResForOutColNameAndValue(String resDataStr){
        return this.outParamResolver.getColNameWithOutParamValue(resDataStr);
    }

}
