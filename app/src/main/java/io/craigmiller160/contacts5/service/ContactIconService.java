package io.craigmiller160.contacts5.service;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import com.amulyakhare.textdrawable.TextDrawable;

import io.craigmiller160.contacts5.R;
import io.craigmiller160.contacts5.util.AbstractAndroidUtil;

/**
 * Created by craig on 6/4/16.
 */
public class ContactIconService extends AbstractAndroidUtil {

    public ContactIconService(Context context){
        super(context);
    }

    public Drawable createContactIcon(String key, char letter){
        TypedArray colors = null;
        TextDrawable defaultPic = null;
        try{
            colors = getResources().obtainTypedArray(R.array.letter_tile_colors);
            int colorIndex = Math.abs(key.hashCode()) % colors.length();
            int contactColor = colors.getColor(colorIndex, Color.BLACK);
            defaultPic = TextDrawable.builder()
                    .beginConfig()
                    .fontSize(100)
                    .bold()
                    .endConfig()
                    .buildRound("" + letter, contactColor);
        }
        finally{
            if(colors != null){
                colors.recycle();
            }
        }

        return defaultPic;
    }

}
