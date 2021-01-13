package chou.may.mypakage.web.exception;

/**
 * 返回名称重名
 * @author lin.xc
 * @date 2021/1/13
 **/
public class ReturnNameDuplicateException extends BuildEnumWebReturnException{

    public ReturnNameDuplicateException() {
    }

    public ReturnNameDuplicateException(String message) {
        super(message);
    }

    public ReturnNameDuplicateException(Throwable cause) {
        super(cause);
    }

    public ReturnNameDuplicateException(String message, Throwable cause) {
        super(message, cause);
    }
}
