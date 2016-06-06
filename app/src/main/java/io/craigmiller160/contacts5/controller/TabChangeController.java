package io.craigmiller160.contacts5.controller;

import android.content.Context;
import android.support.v4.view.ViewPager;

import io.craigmiller160.contacts5.model.ModelFactory;

import static io.craigmiller160.contacts5.util.ContactsConstants.*;

/**
 * Created by craig on 6/5/16.
 */
public class TabChangeController extends AbstractAndroidController implements ViewPager.OnPageChangeListener {

    public TabChangeController(Context context) {
        super(context);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //Do nothing
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //Do nothing
    }
}
