//package io.craigmiller160.contacts5.service;
//
//import android.content.Context;
//import android.content.res.AssetFileDescriptor;
//import android.content.res.ColorStateList;
//import android.content.res.Resources;
//import android.content.res.TypedArray;
//import android.content.res.XmlResourceParser;
//import android.graphics.Movie;
//import android.graphics.drawable.Drawable;
//import android.util.AttributeSet;
//import android.util.TypedValue;
//
//import java.io.InputStream;
//
///**
// * Created by craig on 5/8/16.
// */
//public class ResourceService {
//
//    private final Context context;
//
//    ResourceService(Context context){
//        this.context = context;
//    }
//
//    public CharSequence getText(int id){
//        return context.getResources().getText(id);
//    }
//
//    public CharSequence getQuantityText(int id, int quantity){
//        return context.getResources().getQuantityText(id, quantity);
//    }
//
//    public String getString(int id){
//        return context.getString(id);
//    }
//
//    public String getQuantityString(int id, int quantity){
//        return context.getResources().getQuantityString(id, quantity);
//    }
//
//    public String getQuantityString(int id, int quantity, Object...formatArgs){
//        return context.getResources().getQuantityString(id, quantity, formatArgs);
//    }
//
//    public CharSequence getText(int id, CharSequence def){
//        return context.getResources().getText(id, def);
//    }
//
//    public String[] getStringArray(int id){
//        return context.getResources().getStringArray(id);
//    }
//
//    public int[] getIntArray(int id){
//        return context.getResources().getIntArray(id);
//    }
//
//    public TypedArray obtainTypedArray(int id){
//        return context.getResources().obtainTypedArray(id);
//    }
//
//    public float getDimension(int id){
//        return context.getResources().getDimension(id);
//    }
//
//    public int getDimensionForPixelOffset(int id){
//        return context.getResources().getDimensionPixelOffset(id);
//    }
//
//    public int getDimensionPixelSize(int id){
//        return context.getResources().getDimensionPixelSize(id);
//    }
//
//    public float getFraction(int id, int base, int pbase){
//        return context.getResources().getFraction(id, base, pbase);
//    }
//
//    public Drawable getDrawable(int id, Resources.Theme theme){
//        return context.getResources().getDrawable(id, theme);
//    }
//
//    public Drawable getDrawableForDensity(int id, int density, Resources.Theme theme){
//        return context.getResources().getDrawableForDensity(id, density, theme);
//    }
//
//    public Movie getMovie(int id){
//        return context.getResources().getMovie(id);
//    }
//
//    public boolean getBoolean(int id) throws Resources.NotFoundException {
//        return context.getResources().getBoolean(id);
//    }
//
//    public int getInteger(int id) throws Resources.NotFoundException {
//        return context.getResources().getInteger(id);
//    }
//
//    public XmlResourceParser getLayout(int id) throws Resources.NotFoundException {
//        return context.getResources().getLayout(id);
//    }
//
//    public XmlResourceParser getAnimation(int id) throws Resources.NotFoundException {
//        return context.getResources().getAnimation(id);
//    }
//
//    public XmlResourceParser getXml(int id) throws Resources.NotFoundException {
//        return context.getResources().getXml(id);
//    }
//
//    public InputStream openRawResource(int id) throws Resources.NotFoundException {
//        return context.getResources().openRawResource(id);
//    }
//
//    public InputStream openRawResource(int id, TypedValue value) throws Resources.NotFoundException {
//        return context.getResources().openRawResource(id, value);
//    }
//
//    public AssetFileDescriptor openRawResourceFd(int id) throws Resources.NotFoundException {
//        return context.getResources().openRawResourceFd(id);
//    }
//
//    public void getValue(int id, TypedValue outValue, boolean resolveRefs) throws Resources.NotFoundException {
//        context.getResources().getValue(id, outValue, resolveRefs);
//    }
//
//    public void getValueForDensity(int id, int density, TypedValue outValue, boolean resolveRefs) throws Resources.NotFoundException {
//        context.getResources().getValueForDensity(id, density, outValue, resolveRefs);
//    }
//
//    public String getResourceName(int resid) throws Resources.NotFoundException {
//        return context.getResources().getResourceName(resid);
//    }
//
//    public String getResourcePackageName(int resid) throws Resources.NotFoundException {
//        return context.getResources().getResourcePackageName(resid);
//    }
//
//    public String getResourceTypeName(int resid) throws Resources.NotFoundException {
//        return context.getResources().getResourceTypeName(resid);
//    }
//
//    public String getResourceEntryName(int resid) throws Resources.NotFoundException {
//        return context.getResources().getResourceEntryName(resid);
//    }
//
//}
