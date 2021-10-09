package com.zju.fourinone;

import java.util.logging.Logger;

/**
 * 使用java.util.logging.Logger日志记录工具
 */
public class LogUtil {
    private final static Logger logger = Logger.getLogger("com.zju.fourinone");

    public static void info(String log) {
        logger.info(log);
    }

    public static void warning(String log) {
        logger.warning(log);
    }

    public static void severe(String log) {
        logger.severe(log);
    }
}
