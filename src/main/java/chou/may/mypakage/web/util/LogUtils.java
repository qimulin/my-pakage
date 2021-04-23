package chou.may.mypakage.web.util;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;

/**
 * 日志工具
 * @author Lin.xc
 * @date 2020/7/31
 */
@Component
public final class LogUtils {

    /**
     * 当日志级别在DEBUG以下时，log.debug("hello, this is " + name)就不会执行，从而没有字符串拼接的开销。
     * JIT在运行时会优化if语句，如果isDebugEnabled()返回false, 则JIT会将整个if块全部去掉。
     * */
    public static void logDebugIfEnableDebug(Logger log, String format, Object... args) {
        if(log.isDebugEnabled()){
            log.debug(format, args);
        }
    }

}
