package chou.may.mypakage.web.tailor;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

/**
 * @author lin.xc
 * @date 2021/3/31
 **/
@Data
public class TailorApiParamTreeNode {

    private Integer id;

    private String name;

    private Byte isLeaf;

    private Integer parentId;

    /**
     * 构造函数
     * */
    public TailorApiParamTreeNode(
            Integer id,
            String name,
            Byte isLeaf,
            Integer parentId
    ){
        this.id = id;
        this.name = name;
        this.isLeaf = isLeaf;
        this.parentId = parentId;
    }

    /** 子节点信息 */
    private List<TailorApiParamTreeNode> childrenNodes;

    public TailorApiParamTreeNode addChild(TailorApiParamTreeNode node) {
        if (childrenNodes == null) {
            this.childrenNodes = new LinkedList<>();
        }
        childrenNodes.add(node);
        return this;
    }
}
