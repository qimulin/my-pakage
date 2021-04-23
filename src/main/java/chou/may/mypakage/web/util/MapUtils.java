package chou.may.mypakage.web.util;

import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: lin.xc
 * @Date： 2021-4-23
 * @Explain：Map工具类
 */
public class MapUtils extends HashMap<String, Object> {

    @Override
    public MapUtils put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    /**
     * 添加Map到MapList的每个元素中
     * @author lin.xc
     * @date 2021-4-14
     * */
    public static <K,V> List<Map<K,V>> putAllToMapListElement(List<Map<K,V>> mapList, Map map){
        if(CollectionUtils.isEmpty(mapList)){
            mapList.add(map);
        }else{
            for (Map idx: mapList) {
                idx.putAll(map);
            }
        }
        return mapList;
    }

    /**
     * 笛卡尔积两个Map的数据，相当于全部平铺
     * @author lin.xc
     * @date 2021-4-14
     * @param primaryMapList 主MapList，当另一个MapList为空，将只返回这个主的
     * @param mapList 乘
     * */
    public static <K,V> List<Map<K,V>> cartesianMapList(List<Map<K,V>> primaryMapList, List<Map<K,V>> mapList){
        List<Map<K,V>> result = new ArrayList<>();
        if(CollectionUtils.isEmpty(mapList)){
            return primaryMapList;
        }
        for (Map idx1: primaryMapList) {
            for(Map idx2: mapList){
                Map seg = new HashMap(idx1);
                seg.putAll(idx2);
                result.add(seg);
            }
        }
        return result;
    }
}
