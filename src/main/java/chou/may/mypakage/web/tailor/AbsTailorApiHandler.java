package chou.may.mypakage.web.tailor;

import chou.may.mypakage.web.tailor.resolver.TailorApiResResolver;
import chou.may.mypakage.web.util.HttpClientUtils;
import chou.may.mypakage.web.util.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * @author lin.xc
 * @date 2021/3/31
 **/
public abstract class AbsTailorApiHandler {

    private static final Logger log = LoggerFactory.getLogger(AbsTailorApiHandler.class);

    /** 可以选择自行拼接url，不传params，也可以传params, 定制接口通常是一组请求接口地址相同 */
    protected String url;
    /** 接口响应处理器 */
    protected TailorApiResResolver resResolver;

    public AbsTailorApiHandler(
            String url,
            TailorApiResResolver resResolver
    ){
        Assert.hasText(url, "定制接口请求地址不能设置为空！");
        Assert.notNull(resResolver, "定制接口响应解析器不能设置为空！");
        this.url = url;
        this.resResolver = resResolver;
        log.info("定制接口处理器选择的响应解析器为“{}”，说明：{}", resResolver.getName(), resResolver.getNote());
    }

    /**
     * 获取响应解析器
     * */
    public TailorApiResResolver getTailorApiResResolver(){
        return this.resResolver;
    }

    /**
     * 设置地址
     * 只有子类才可以重写
     * */
    protected void setUrl(String url){
        this.url = url;
    }

    /**
     * Get请求
     * @param params 请求参数
     * */
    protected String get(
            Map<String, String> params
    ){
        return get(null, params);
    }

    /**
     * Get请求
     * @param header 头部域
     * @param params 请求参数
     * */
    protected String get(
            Map<String, String> header,
            Map<String, String> params
    ){
        if(log.isDebugEnabled()){
            log.debug("定制接口GET请求地址：{}", this.url);
            log.debug("定制接口GET请求头部域：{}", header);
            log.debug("定制接口GET请求参数：{}", params);
        }
        String result = HttpClientUtils.doGetRequest(this.url, header, params);
        return result;
    }

    /**
     * 请求并返回数据
     * TODO：临时这样方便，后面参数有需要加header之类的具体再改造
     * */
    public String requestAndReturnDataStr(Map<String, String> params){
        String responseStr = get(params);
        LogUtils.logDebugIfEnableDebug(log,"定制接口GET请求响应：{}", responseStr);
        return resResolver.checkAndGetEffectiveResDataStr(responseStr);
    }

}
