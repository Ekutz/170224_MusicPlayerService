package com.jspark.android.musicplayerservice;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by jsPark on 2017. 2. 8..
 */

public class Message {
    public static void show(Context context, String str) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }
}
