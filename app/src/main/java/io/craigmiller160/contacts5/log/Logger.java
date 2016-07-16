package io.craigmiller160.contacts5.log;

import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrBuilder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.craigmiller160.contacts5.IllegalArgumentCtxException;

/**
 * Created by craig on 7/3/16.
 */
public class Logger {

    public static final String DEBUG = "DEBUG";
    public static final String ERROR = "ERROR";
    public static final String INFO = "INFO";
    public static final String VERBOSE = "VERBOSE";
    public static final String WARN = "WARN";
    public static final String WTF = "WTF";

    private static final DateFormat dateFormat = new SimpleDateFormat("MM-dd kk:mm:ss.SSS");
    private static final String steFirstLineIndent = "               ";
    private static final String steOtherIndent = "                  ";

    private final String tag;
    private final LogCache cache = LogCache.getCache();

    public static Logger newLogger(String tag){
        if(tag == null || tag.length() > 23){
            throw new IllegalArgumentCtxException("Logger tag cannot be null and maximum length is 23 characters")
                    .addContextValue("Tag", tag)
                    .addContextValue("Tag Length", tag != null ? tag.length() : 0);
        }

        return new Logger(tag);
    }

    private Logger(String tag){
        this.tag = tag;
    }

    public int d(String tag, String msg, Throwable t){
        cacheLog(DEBUG, tag, msg, t);
        return Log.d(tag, msg, t);
    }

    public int d(String tag, String msg){
        cacheLog(DEBUG, tag, msg, null);
        return Log.d(tag, msg);
    }

    public int e(String tag, String msg){
        cacheLog(ERROR, tag, msg, null);
        return Log.e(tag, msg);
    }

    public int e(String tag, String msg, Throwable t){
        cacheLog(ERROR, tag, msg, t);
        return Log.e(tag, msg, t);
    }

    public int i(String tag, String msg){
        cacheLog(INFO, tag, msg, null);
        return Log.i(tag, msg);
    }

    public int i(String tag, String msg, Throwable t){
        cacheLog(INFO, tag, msg, t);
        return Log.i(tag, msg, t);
    }

    public int v(String tag, String msg){
        cacheLog(VERBOSE, tag, msg, null);
        return Log.v(tag, msg);
    }

    public int v(String tag, String msg, Throwable t){
        cacheLog(VERBOSE, tag, msg, t);
        return Log.v(tag, msg, t);
    }

    public int w(String tag, String msg){
        cacheLog(WARN, tag, msg, null);
        return Log.w(tag, msg);
    }

    public int w(String tag, String msg, Throwable t){
        cacheLog(WARN, tag, msg, t);
        return Log.w(tag, msg, t);
    }

    public int w(String tag, Throwable t){
        cacheLog(WARN, tag, null, t);
        return Log.w(tag, t);
    }

    public int wtf(String tag, String msg){
        cacheLog(WTF, tag, msg, null);
        return Log.wtf(tag, msg);
    }

    public int wtf(String tag, String msg, Throwable t){
        cacheLog(WTF, tag, msg, t);
        return Log.wtf(tag, msg, t);
    }

    public int wtf(String tag, Throwable t){
        cacheLog(WTF, tag, null, t);
        return Log.wtf(tag, t);
    }

    private void cacheLog(String level, String tag, String msg, Throwable t){
        StrBuilder builder = new StrBuilder()
                .append(dateFormat.format(new Date()))
                .append(" ").append(level).append(" ")
                .append("io.craigmiller160.contacts5")
                .append(" ").append(tag).append(": ");

        if(!StringUtils.isEmpty(msg)){
            builder.append(msg);
        }

        if(t != null){
            StackTraceElement[] steArr = t.getStackTrace();
            for(int i = 0; i < steArr.length; i++){
                builder.appendNewLine();
                if(i == 0){
                    builder.append(steFirstLineIndent);
                }
                else{
                    builder.append(steOtherIndent);
                }
                builder.append(steArr[i].toString());
            }
        }
        cache.writeLogEntry(builder.toString());
    }

    public void flushCache(){
        cache.flushCache();
    }

}
