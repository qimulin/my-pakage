package chou.may.mypakage.web.tailor;

import chou.may.mypakage.web.tailor.resolver.ResResolver;
import chou.may.mypakage.web.util.HttpClientUtils;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * @author lin.xc
 * @date 2021/3/31
 **/
public abstract class AbsTailorApiHandler {

    Logger log = LoggerFactory.getLogger(AbsTailorApiHandler.class);

    /** 可以选择自行拼接url，不传params，也可以传params */
    protected String url;
    protected Map<String, String> header;
    protected Map<String, String> params;
    /** 接口响应处理器 */
    private ResResolver resResolver;

    public AbsTailorApiHandler(
            String url,
            ResResolver resResolver
    ){
        this.url = url;
        this.resResolver = resResolver;
        log.info("定制接口处理器选择的响应解析器为“{}”，说明：{}", resResolver.getName(), resResolver.getNote());
    }

    /**
     * 设置地址
     * 只有子类才可以重写
     * */
    protected void setUrl(String url){
        this.url = url;
    }

    /**
     * 设置头部域
     * */
    public void setHeader(Map<String, String> header){
        this.header = header;
    }

    /**
     * 设置参数
     * */
    public void setParams(Map<String, String> params){
        this.params = params;
    }

    /**
     * Get请求
     * */
    protected String doGet(){
        checkBeforeDoGet();
        log.debug("定制接口GET请求地址：{}", this.url);
        log.debug("定制接口GET请求头部域：{}", this.header);
        log.debug("定制接口GET请求参数：{}", this.params);
        String result = HttpClientUtils.doGetRequest(this.url, this.header, this.params);
        return result;
    }

    /**
     * 执行Get并返回数据
     * */
    public JSONObject doGetAndReturnData(){
        String result = doGet();
        log.debug("定制接口GET请求响应：{}", result);
        return resResolver.resolveResDataToJson(result);
    }

    private void checkBeforeDoGet(){
        Assert.hasText(this.url, "定制接口请求地址不可传空");
    }

}
