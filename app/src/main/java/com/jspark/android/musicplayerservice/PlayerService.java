package com.jspark.android.musicplayerservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import static com.jspark.android.musicplayerservice.App.PAUSE;
import static com.jspark.android.musicplayerservice.App.PLAY;
import static com.jspark.android.musicplayerservice.App.audio;
import static com.jspark.android.musicplayerservice.App.playStatus;

public class PlayerService extends Service {

    public PlayerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    private void playStart() {
        audio.start();
        playStatus=PLAY;
    }

    private void playPause() {
        audio.pause();
        playStatus = PAUSE;
    }

    private void playRestart() {
        audio.start();
        playStatus = PLAY;
    }
}
