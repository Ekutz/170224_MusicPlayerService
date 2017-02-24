package com.jspark.android.musicplayerservice;

import android.media.MediaPlayer;

/**
 * Created by jsPark on 2017. 2. 24..
 */

public class App {

    public static MediaPlayer audio = null;
    public static int position = 0;

    public static final int PLAY = 0;
    public static final int PAUSE = 1;
    public static final int STOP = 2;

    public static int playStatus = STOP;
}
