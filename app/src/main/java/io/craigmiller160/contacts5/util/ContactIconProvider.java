package io.craigmiller160.contacts5.util;

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
public class ContactIconProvider extends AbstractAndroidUtil {

    public static final int CIRCLE = 401;
    public static final int SQUARE = 402;

    public ContactIconProvider(Context context){
        super(context);
    }

    private Drawable createContactIcon(String key, char letter, int shape){
        TypedArray colors = null;
        TextDrawable defaultPic = null;
        try{
            colors = getResources().obtainTypedArray(R.array.letter_tile_colors);
            int colorIndex = Math.abs(key.hashCode()) % colors.length();
            int contactColor = colors.getColor(colorIndex, Color.BLACK);

            TextDrawable.IShapeBuilder builder = TextDrawable.builder()
                    .beginConfig()
                    .fontSize(100)
                    .bold()
                    .endConfig();

            switch (shape){
                case CIRCLE:
                    defaultPic = builder.buildRound("" + letter, contactColor);
                    break;
                case SQUARE:
                    defaultPic = builder.buildRect("" + letter, contactColor);
                    break;
            }
        }
        finally{
            if(colors != null){
                colors.recycle();
            }
        }

        return defaultPic;
    }

    public Drawable createCircleContactIcon(String key, char letter){
        return createContactIcon(key, letter, CIRCLE);
    }

    public Drawable createSquareContactIcon(String key, char letter){
        return createContactIcon(key, letter, SQUARE);
    }

}
