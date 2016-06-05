//package io.craigmiller160.contacts5.model;
//
//import android.content.Context;
//import android.util.AndroidRuntimeException;
//
//import static io.craigmiller160.contacts5.util.ContactsConstants.*;
//
///**
// * Created by craig on 6/4/16.
// */
//public class ContactAppModelFactory extends AbstractModelFactory {
//
//    private static ContactAppModelFactory instance;
//    private static final Object instanceLock = new Object();
//
//    protected ContactAppModelFactory(Context context) {
//        super(context);
//        init();
//    }
//
//    private void init(){
//        registerModel(CONTACTS_MODEL, new ContactsModel());
//    }
//
//    public static void initialize(Context context){
//        if(instance == null){
//            synchronized (instanceLock){
//                if(instance == null){
//                    instance = new ContactAppModelFactory(context);
//                }
//                else{
//                    throw new AndroidRuntimeException("ServiceFactory can only be initialized once");
//                }
//            }
//        }
//        else{
//            throw new AndroidRuntimeException("ServiceFactory can only be initialized once");
//        }
//    }
//
//    public static boolean isInitialized(){
//        synchronized (instanceLock){
//            return instance != null;
//        }
//    }
//
//    public static ContactAppModelFactory getInstance(){
//        if(instance == null){
//            synchronized (instanceLock){
//                if(instance == null){
//                    throw new AndroidRuntimeException("ServiceFacotry is not initialized");
//                }
//            }
//        }
//        return instance;
//    }
//
//}
