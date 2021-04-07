package chou.may.mypakage.web.tailor;

import chou.may.mypakage.web.tailor.resolver.ResResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.util.DigestUtils;

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

    public YsApiHandler(String url, ResResolver resResolver) {
        super(url, resResolver);
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public void setRequestSecretRedisKey(String requestSecretRedisKey) {
        this.requestSecretRedisKey = requestSecretRedisKey;
    }

    /**
     * 此处注意：appKey和request不要带到params里来
     * */
    @Override
    public void setParams(Map<String, String> params) {
        Assert.hasText(appKey, "appKey不可传空");
        // 获取参数传过来的请求秘钥
        String requestSecret = "Redis工具去请求获取到这个值";
        Assert.hasText(requestSecret, "请检查Redis键"+this.requestSecretRedisKey+"是否有值！");
        log.debug("从Redis获取有数接口请求秘钥为：{}", requestSecret);
        Assert.hasText(requestSecret, "requestSecret不可传空");
        // 获取请求时间戳
        long requestTime = System.currentTimeMillis();
        // 构建sign
        String str = appKey + requestSecret + requestTime;
        String sign = DigestUtils.md5DigestAsHex(str.getBytes()).toLowerCase();
        // 添加安全验证的参数
        params.put("requestTime", String.valueOf(requestTime));
        params.put("appKey", appKey);
        params.put("sign", sign);
        super.params = params;
    }

}
