package chou.may.mypakage.web.tailor.exception;

/**
 * 接口调用失败
 * @author lin.xc
 * @date 2021/1/13
 **/
public class CallFailedException extends RuntimeException {

    public CallFailedException() {
    }

    public CallFailedException(String message) {
        super(buildErrorMessage(message));
    }

    public CallFailedException(Throwable cause) {
        super(cause);
    }

    public CallFailedException(String message, Throwable cause) {
        super(buildErrorMessage(message), cause);
    }

    public static final String buildErrorMessage(String message){
        return "定制接口调用失败！信息：".concat(message);
    }
}
