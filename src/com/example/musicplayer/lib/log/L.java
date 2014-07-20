package com.example.musicplayer.lib.log;

import android.util.Log;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: xiejm
 * Date: 7/25/13
 * Time: 6:32 PM
 */
public class L {
    public final static boolean DEBUG = true;

    private static PrintWriter sLogWriter;
    private static boolean sWriteFile = false;

    private static SimpleDateFormat sSdf;

    public static void setupWriteToFile(String logFilePath) {
        if (DEBUG) {
            try {
                sLogWriter = new PrintWriter(new FileWriter(logFilePath, true));
                sSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
                sWriteFile = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void log(int type, String message) {
        StackTraceElement stackTrace = Thread.currentThread().getStackTrace()[4];
        String className = stackTrace.getClassName();
        String tag = className.substring(className.lastIndexOf('.') + 1) + "." + stackTrace.getMethodName() + "#" + stackTrace.getLineNumber();

        switch (type) {
            case Log.DEBUG:
                if (sWriteFile) {
                    writeLogToFile(" [D] ", message);
                } else {
                    Log.d(tag, message);
                }
                break;

            case Log.INFO:
                if (sWriteFile) {
                    writeLogToFile(" [I] ", message);
                } else {
                    Log.i(tag, message);
                }
                break;

            case Log.WARN:
                if (sWriteFile) {
                    writeLogToFile(" [W] ", message);
                } else {
                    Log.w(tag, message);
                }
                break;

            case Log.ERROR:
                if (sWriteFile) {
                    writeLogToFile(" [E] ", message);
                } else {
                    Log.e(tag, message);
                }
                break;

            case Log.VERBOSE:
                if (sWriteFile) {
                    writeLogToFile(" [V] ", message);
                } else {
                    Log.v(tag, message);
                }
                break;
        }
    }

    private static void writeLogToFile(String tag, String message) {
        sLogWriter.write(sSdf.format(new Date()));
        sLogWriter.write(tag);
        sLogWriter.write(message);
        sLogWriter.write('\n');
        sLogWriter.flush();
    }

    public static void d(String fmt, Object ... args) {
        if (L.DEBUG) {
            if (args == null || args.length == 0) {
                log(Log.DEBUG, fmt);
            } else {
                log(Log.DEBUG, String.format(fmt, args));
            }
        }
    }

    public static void i(String fmt, Object ... args) {
        if (L.DEBUG) {
            if (args == null || args.length == 0) {
                log(Log.INFO, fmt);
            } else {
                log(Log.INFO, String.format(fmt, args));
            }
        }
    }

    public static void w(String fmt, Object ... args) {
        if (L.DEBUG) {
            if (args == null || args.length == 0) {
                log(Log.WARN, fmt);
            } else {
                log(Log.WARN, String.format(fmt, args));
            }
        }
    }

    public static void e(String fmt, Object ... args) {
        if (L.DEBUG) {
            if (args == null || args.length == 0) {
                log(Log.ERROR, fmt);
            } else {
                log(Log.ERROR, String.format(fmt, args));
            }
        }
    }

    public static void v(String fmt, Object ... args) {
        if (L.DEBUG) {
            if (args == null || args.length == 0) {
                log(Log.VERBOSE, fmt);
            } else {
                log(Log.VERBOSE, String.format(fmt, args));
            }
        }
    }
}

