package io.craigmiller160.contacts5.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import io.craigmiller160.contacts5.R;

/**
 * Created by craig on 5/4/16.
 */
public class ContactsActivity extends AppCompatActivity {

    private static final String TAG = "ContactsActivity";

    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_contacts);
    }

}
