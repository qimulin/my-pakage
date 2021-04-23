package chou.may.mypakage.web.tailor.api.exception;

/**
 * 接口返回信息错误
 * @author lin.xc
 * @date 2021/1/13
 **/
public class ResolveErrorException extends RuntimeException {

    public ResolveErrorException() {
    }

    public ResolveErrorException(String message) {
        super(buildErrorMessage(message));
    }

    public ResolveErrorException(Throwable cause) {
        super(cause);
    }

    public ResolveErrorException(String message, Throwable cause) {
        super(buildErrorMessage(message), cause);
    }

    public static final String buildErrorMessage(String message){
        return "解析响应数据错误！信息：".concat(message);
    }
}
