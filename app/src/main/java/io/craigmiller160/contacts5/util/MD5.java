package io.craigmiller160.contacts5.util;

import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by craig on 6/30/16.
 */
public class MD5 {

    private static final String TAG = "MD5";

    public static String encode(String value){
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(value.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (int i=0; i<messageDigest.length; i++){
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            }
            return hexString.toString().toUpperCase();

        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "No MD5 algorithm available");
        }
        return "";
    }

}
