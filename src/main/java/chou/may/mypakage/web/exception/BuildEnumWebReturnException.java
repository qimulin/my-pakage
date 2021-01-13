package chou.may.mypakage.web.exception;

/**
 * 构建枚举Web返回异常
 * @author lin.xc
 * @date 2021/1/13
 **/
public class BuildEnumWebReturnException extends RuntimeException {

    public BuildEnumWebReturnException() {
    }

    public BuildEnumWebReturnException(String message) {
        super(message);
    }

    public BuildEnumWebReturnException(Throwable cause) {
        super(cause);
    }

    public BuildEnumWebReturnException(String message, Throwable cause) {
        super(message, cause);
    }
}
