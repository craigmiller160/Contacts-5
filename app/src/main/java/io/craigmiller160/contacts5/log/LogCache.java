package io.craigmiller160.contacts5.log;

import android.content.Context;
import android.os.Environment;
import android.support.v4.os.EnvironmentCompat;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AndroidRuntimeException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import io.craigmiller160.contacts5.util.AbstractAndroidUtil;

/**
 * Created by craig on 7/3/16.
 */
public class LogCache extends AbstractAndroidUtil{

    private static final DateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss.SSS");
    private static final String reportStart = "Contacts 5+ Debug Report - %s";

    private static final Object instanceLock = new Object();
    private static LogCache instance;

    private final List<String> logs;
    private int entryCount = 100;

    public static LogCache getCache(){
        if(instance == null){
            synchronized (instanceLock){
                if(instance == null){
                    throw new AndroidRuntimeException("Error! LogCache not initialized.");
                }
            }
        }
        return instance;
    }

    public static void initialize(Context context){
        if(instance == null){
            synchronized (instanceLock){
                if(instance == null){
                    instance = new LogCache(context);
                }
                else{
                    throw new AndroidRuntimeException("Error! Cannot initialize LogCache more than once.");
                }
            }
        }
    }

    private LogCache(Context context){
        super(context);
        logs = Collections.synchronizedList(new ArrayList<String>());
    }

    public synchronized void setCacheSize(int entryCount){
        this.entryCount = entryCount;
    }

    public synchronized void writeLogEntry(String logEntry){
        logs.add(logEntry);
        if(entryCount < logs.size()){
            logs.remove(0);
        }
    }

    public synchronized void flushCache() {
        File filesDir = getContext().getExternalFilesDir(null);
        File logFile = new File(filesDir, "contacts5-debug.log");
        try(FileWriter writer = new FileWriter(logFile)){
            writer.write(String.format(reportStart, dateFormat.format(new Date())));
            writer.write(System.lineSeparator() + System.lineSeparator());
            for(String entry : logs){
                writer.write(entry);
                writer.write(System.lineSeparator());
            }
        }
        catch(IOException ex){
            throw new AndroidRuntimeException("Failed to flush LogCache", ex);
        }
    }

}
