package io.craigmiller160.contacts5.old.fragment;

import android.support.v4.app.Fragment;

import io.craigmiller160.contacts5.old.application.AbstractAndroidMVPApp;

/**
 * Created by Craig on 2/23/2016.
 */
public class AbstractMVPFragment extends Fragment {

    protected final AbstractAndroidMVPApp getMVPApplication(){
        return (AbstractAndroidMVPApp) getActivity().getApplicationContext();
    }

}
