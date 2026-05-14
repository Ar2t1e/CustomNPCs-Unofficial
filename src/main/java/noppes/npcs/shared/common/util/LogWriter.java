package noppes.npcs.shared.common.util;

import noppes.npcs.CustomNpcs;
import org.apache.logging.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.logging.log4j.Level.*;

public class LogWriter {

    private static void log(Level level, Object msg) {
        StackTraceElement caller = Thread.currentThread().getStackTrace()[3];
        Logger logger = LoggerFactory.getLogger(caller.getClassName());
        String clss = caller.getClassName();
        if (clss.contains(".")) { clss = clss.substring(clss.lastIndexOf(".") + 1); }
        String message;
        if (caller.getClassName().contains(".DataDebug")) { message = msg.toString(); }
        else if (msg.toString().startsWith("found class:")) {
            String[] lines = msg.toString().split(":");
            try {
                Class<?> cl = Class.forName(lines[1]);
                message = "(" + cl.getSimpleName() + ".java:" + lines[2] + ") - \"" + lines[1] + "\"";
            }
            catch (Exception ignored) {
                message = "(" + lines[1].substring(lines[1].lastIndexOf(".") + 1) + ".java:" + lines[2] + ") - \"" + lines[1] + "\"";
            }
        }
        else { message = "(" + clss + ".java:" + caller.getLineNumber() + ") \"" + msg + "\""; }
        if (level.equals(TRACE)) { logger.trace(message);}
        else if (level.equals(DEBUG)) { logger.debug(message); }
        else if (level.equals(WARN)) { logger.warn(message); }
        else if (level.equals(ERROR)) { logger.error(message); }
        else { logger.info(message); }
    }

    public static void debug(String msg) {
        if (CustomNpcs.VerboseDebug && msg != null && !msg.trim().isEmpty()) {
            log(INFO, msg);
        }
    }

    public static void error(Object msg) {
        if (msg == null || msg.toString().isEmpty()) { return; }
        if (msg instanceof Throwable e) {
            StringBuilder message = new StringBuilder(" \"").append(e).append("\":");
            for (StackTraceElement traceElement : e.getStackTrace()) { message.append("\n\tat ").append(traceElement); }
            log(Level.ERROR, message.toString());
        }
        else { log(Level.ERROR, msg); }
    }

    public static void error(Object msg, Throwable e) {
        StringBuilder message = new StringBuilder();
        if (msg != null && !msg.toString().isEmpty()) { message = new StringBuilder(msg.toString()); }
        if (e != null) {
            message.append(" \"").append(e).append("\":");
            for (StackTraceElement traceElement : e.getStackTrace()) { message.append("\n\tat ").append(traceElement); }
        }
        log(Level.ERROR, message.toString());
    }

    public static void except(Throwable e) {
        if (e == null) { return; }
        StringBuilder message = new StringBuilder(e.toString());
        message.append(" \"").append(e).append("\":");
        for (StackTraceElement traceElement : e.getStackTrace()) { message.append("\n\tat ").append(traceElement); }
        log(Level.FATAL, message.toString());
    }

    public static void pathInfo(Object msg, int maxLines) {
        if (msg == null || msg.toString().isEmpty()) { return; }
        StringBuilder message = new StringBuilder(msg + ":");
        StackTraceElement[] stackTraces = Thread.currentThread().getStackTrace();
        for (int i = 2; i < stackTraces.length && (maxLines < 1 || i < maxLines + 2); i++) { message.append("\n\tat ").append(stackTraces[i]); }
        log(Level.INFO, message.toString());
    }

    public static void info(Object msg) {
        if (msg == null || msg.toString().isEmpty()) { return; }
        log(INFO, msg);
    }

    public static void warn(Object msg) {
        if (msg == null || msg.toString().isEmpty()) { return; }
        StringBuilder message = new StringBuilder(msg + ":");
        StackTraceElement[] stackTraces = Thread.currentThread().getStackTrace();
        for (int i = 2; i < stackTraces.length && i < 7; i++) { message.append("\n\tat ").append(stackTraces[i]); }
        log(Level.WARN, message.toString());
    }

}
