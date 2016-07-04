package io.craigmiller160.contacts5.log;

import android.content.Context;
import android.util.AndroidRuntimeException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.craigmiller160.contacts5.IllegalArgumentCtxException;
import io.craigmiller160.contacts5.util.AbstractAndroidUtil;

/**
 * Created by craig on 7/3/16.
 */
public class LogCache extends AbstractAndroidUtil{

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

    public synchronized void flushCache(){
        File filesDir = getContext().getFilesDir();
        try(FileWriter writer = new FileWriter(filesDir)){
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
