package chou.may.wechat.custom.sal.response;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 基本的响应内容
 * 主要是存错误code和错误message，方便上层自己处理错误
 * @author Lin.xc
 * @date 2020/2/18
 */
@Data
public class BaseResponse<T>{
    /**
     * 自定义业务code，失败的全部由微信返回
     * */
    // 成功
    public static final String CODE_SUCCESS = "SUCCESS";
    // 未知
    public static final String CODE_UNKNOWN = "UNKNOWN";

    /**
     * 响应数据（HTTP Status=200返回）
     * */
    private T data;
    /**
     * 状态码，自定义默认成功为SUCCESS
     * */
    @JSONField(name="code")
    private String code = "SUCCESS";
    /**
     * 错误详情
     * */
    @JSONField(name="detail")
    private Detail detail;
    /**
     * 错误信息
     * */
    @JSONField(name="message")
    private String message;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Detail getDetail() {
        return detail;
    }

    public void setDetail(Detail detail) {
        this.detail = detail;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 判断是否成功
     * */
    public boolean isSuccess(){
        return CODE_SUCCESS.equals(this.getCode());
    }

    @Data
    public static class Detail{
       private String field;
       private String location;
       private String value;
    }
}
