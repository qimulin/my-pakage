package chou.may.mypakage.web.tailor.api;

import chou.may.mypakage.web.service.AuxTableInfoService;
import chou.may.mypakage.web.tailor.api.resolver.TailorApiResResolver;
import chou.may.mypakage.web.util.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 有数接口处理
 * @author lin.xc
 * @date 2021/3/31
 **/
@Slf4j
public class YsApiHandler extends AbsTailorApiHandler {

    /** 应用key，通常需要去申请 */
    private String appKey;
    /** 请求秘钥Redis键 */
    private String requestSecretRedisKey;

    /** 辅助表名称（一般作用于具有接口调用次数限制的接口组件，再次运行的时候，先去辅助表中取数据） */
    private String auxTableName;
    /** 辅助表信息服务 */
    private AuxTableInfoService auxTableInfoService;

    /** API缓存相关表的返回数据字段名 */
    public static final String RES_DATA_FIELD_NAME = "res_data";

    public YsApiHandler(String url, TailorApiResResolver resResolver) {
        super(url, resResolver);
    }

    public void setAppKey(String appKey) {
        Assert.hasText(appKey, "appKey不可传空");
        this.appKey = appKey;
    }

    public void setRequestSecretRedisKey(String requestSecretRedisKey) {
        this.requestSecretRedisKey = requestSecretRedisKey;
    }

    public void setAuxTableName(String auxTableName) {
        log.info("有数接口处理器设置缓存表名称为：{}", auxTableName);
        this.auxTableName = auxTableName;
    }

    public void setAuxTableInfoService(AuxTableInfoService auxTableInfoService) {
        this.auxTableInfoService = auxTableInfoService;
    }

    @Override
    public String requestAndReturnDataStr(Map<String, String> bizParams){
        // 响应结果
        String result;
        if(StringUtils.isNotBlank(this.auxTableName)){
            LogUtils.logDebugIfEnableDebug(log,"走缓存表[tableName={}]查询", this.auxTableName);
            // 存在缓存表，先查缓存表数据
            List<Map> dbResData = auxTableInfoService.listAuxTableData(this.auxTableName, Arrays.asList(RES_DATA_FIELD_NAME), bizParams);
            if(CollectionUtils.isEmpty(dbResData)){
                LogUtils.logDebugIfEnableDebug(log,"未查询获取到数据！转走接口查询。参数：{}", bizParams.toString());
                result = this.doRequestAndReturnDataStr(bizParams);
                // 若查询到的数据不为空，则存储该数据到缓存表
                if(StringUtils.isNotBlank(result)){
                    Map<String, Object> colNameWithValueMap = new HashMap<>();
                    // 将参数先加入
                    colNameWithValueMap.putAll(bizParams);
                    // 存入结果数据
                    colNameWithValueMap.put(RES_DATA_FIELD_NAME, result);
                    // 存储数据到缓存表
                    auxTableInfoService.insertAuxTableDataIgnoreException(this.auxTableName, colNameWithValueMap);
                }
            }else{
                // 查到数据，则处理 【注意】可用索引控制下条件字段不能查询出多条数据
                Map cacheData = dbResData.get(0);
                LogUtils.logDebugIfEnableDebug(log,"查询获取到数据：{}", cacheData.toString());
                // 返回数据字符串，转Json对象
                result = cacheData.get(RES_DATA_FIELD_NAME).toString();
            }
        }else{
            // 无设置缓存表，则执行走接口查询
            result = this.doRequestAndReturnDataStr(bizParams);
        }
        return result;
    }

    /**
     * 构建实际请求参数
     * 此处注意：appKey和requestSecret不要带到params里来
     * */
    public Map<String, String> buildRequestParams(Map<String, String> bizParams) {
        // 将业务参数赋值给请求参数
        Map<String, String> requestParams = new HashMap<>(bizParams);
        // 获取参数传过来的请求秘钥
        String requestSecret = "Redis去获取";
        Assert.hasText(requestSecret, "请检查Redis键"+this.requestSecretRedisKey+"是否有值！");
        LogUtils.logDebugIfEnableDebug(log,"从Redis获取有数接口请求秘钥为：{}", requestSecret);
        Assert.hasText(requestSecret, "requestSecret不可为空");
        // 获取请求时间戳
        long requestTime = System.currentTimeMillis();
        // 构建sign
        String str = this.appKey + requestSecret + requestTime;
        LogUtils.logDebugIfEnableDebug(log,"构建Sign原始字符串为：{}", str);
        String sign = DigestUtils.md5DigestAsHex(str.getBytes(StandardCharsets.UTF_8)).toLowerCase();
        // 添加安全验证的参数
        requestParams.put("requestTime", String.valueOf(requestTime));
        requestParams.put("appKey", appKey);
        requestParams.put("sign", sign);
        return requestParams;
    }

    /**
     * 根据业务参数执行请求
     * */
    public String doRequestAndReturnDataStr(Map<String, String> bizParams){
        Map<String, String> requestParams = buildRequestParams(bizParams);
        return super.requestAndReturnDataStr(requestParams);
    }

}
