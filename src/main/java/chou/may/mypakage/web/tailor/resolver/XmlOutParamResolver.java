package chou.may.mypakage.web.tailor.resolver;

import chou.may.mypakage.web.tailor.TailorApiParamTreeNode;
import chou.may.mypakage.web.tailor.exception.ResolveErrorException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Xml类型的参数处理
 * @author lin.xc
 * @date 2021/4/14
 **/
public class XmlOutParamResolver extends AbsTailorApiOutParamResolver<Element>{

    /**
     * 构造函数
     * */
    public XmlOutParamResolver(
            Set<TailorApiParamTreeNode> outParamTreeNodes,
            Map<Integer, String> outParamIdWithColNameMap
    ) {
        super(outParamTreeNodes, outParamIdWithColNameMap);
    }

    @Override
    protected Element parseStrToElement(String elementStr) {
        return getXmlRootElement(elementStr);
    }

    @Override
    protected List<Element> parseStrToElements(String elementsStr) {
        // 似乎不会存在这种情况，都是从根节点往下再一对多的。所以抛异常
        throw new RuntimeException("不支持！请检查该种格式");
    }

    @Override
    protected Object getLeafChildNodeValue(Element currElement, String childNodeName) {
        Element childElement = getElementChildNodeValue(currElement, childNodeName);
        if(childElement==null){
            return null;
        }
        return childElement.getData();
    }

    @Override
    protected Element getElementChildNodeValue(Element currElement, String childNodeName) {
        return currElement.element(childNodeName);
    }

    @Override
    protected List<Element> getElementsChildNodeValue(Element currElement, String childNodeName) {
        return currElement.elements(childNodeName);
    }

    /**
     * 获取xml的根元素
     * @param xmlStr xml字符串数据
     * */
    private static Element getXmlRootElement(String xmlStr){
        try {
            Document doc = DocumentHelper.parseText(xmlStr);
            return doc.getRootElement();
        } catch (DocumentException e) {
            e.printStackTrace();
            throw new ResolveErrorException(e.getMessage().concat("响应数据为：").concat(xmlStr));
        }
    }

}
