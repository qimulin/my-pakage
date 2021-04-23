package chou.may.mypakage.web.tailor.exception;

/**
 * 接口返回信息错误
 * @author lin.xc
 * @date 2021/1/13
 **/
public class ReturnDataErrorException extends RuntimeException {

    public ReturnDataErrorException() {
    }

    public ReturnDataErrorException(String message) {
        super(buildErrorMessage(message));
    }

    public ReturnDataErrorException(Throwable cause) {
        super(cause);
    }

    public ReturnDataErrorException(String message, Throwable cause) {
        super(buildErrorMessage(message), cause);
    }

    public static final String buildErrorMessage(String message){
        return "定制接口返回数据错误！信息：".concat(message);
    }
}
