package io.craigmiller160.contacts5.controller;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.SearchView;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import io.craigmiller160.contacts5.R;

/**
 * Created by craig on 6/25/16.
 */
public class SearchController extends AbstractAndroidController implements SearchView.OnCloseListener, SearchView.OnQueryTextListener, MenuItemCompat.OnActionExpandListener{

    public SearchController(AppCompatActivity activity) {
        super(activity);
    }

    public SearchController(AppCompatActivity activity, Map<String,Object> args){
        super(activity, args);
    }

    public AppCompatActivity getActivity(){
        return (AppCompatActivity) getContext();
    }

    @Override
    public boolean onClose() {
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        getActivity().findViewById(R.id.contacts_tabs).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.add_contact_fab).setVisibility(View.VISIBLE);
//        getActivity().getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        return true;
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        getActivity().findViewById(R.id.contacts_tabs).setVisibility(View.GONE);
        getActivity().findViewById(R.id.add_contact_fab).setVisibility(View.GONE);
//        getActivity().getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        SearchView searchView = getArg(R.string.search_view, SearchView.class);
        int id = searchView.getContext().getResources().getIdentifier("android:id/search_plate", null, null);
        View searchPlate = searchView.findViewById(id);

        return true;
    }

//    public static <V extends View> Collection<V> findChildrenByClass(ViewGroup viewGroup, Class<V> clazz) {
//
//        return gatherChildrenByClass(viewGroup, clazz, new ArrayList<V>());
//    }
//
//    private static <V extends View> Collection<V> gatherChildrenByClass(ViewGroup viewGroup, Class<V> clazz, Collection<V> childrenFound) {
//
//        for (int i = 0; i < viewGroup.getChildCount(); i++)
//        {
//            final View child = viewGroup.getChildAt(i);
//            System.out.println("View: " + child.getClass().getName());
//            if (clazz.isAssignableFrom(child.getClass())) {
//                System.out.println("Adding result");
//                childrenFound.add((V)child);
//            }
//            if (child instanceof ViewGroup) {
//                gatherChildrenByClass((ViewGroup) child, clazz, childrenFound);
//            }
//        }
//
//        return childrenFound;
//    }

    private static <V extends View> Collection<V> findChildrenByClass(ViewGroup viewGroup, Class<V> clazz){
        List<V> list = new ArrayList<>();
        for(int i = 0; i < viewGroup.getChildCount(); i++){
            View child = viewGroup.getChildAt(i);
            if(clazz.isAssignableFrom(child.getClass())){
                list.add((V) child);
            }
            if(child instanceof ViewGroup){
                list.addAll(findChildrenByClass((ViewGroup) child, clazz));
            }
        }
        return list;
    }
}
