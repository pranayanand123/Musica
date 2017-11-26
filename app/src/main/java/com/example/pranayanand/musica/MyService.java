package com.example.pranayanand.musica;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

import java.io.IOException;

/**
 * Created by Pranay Anand on 12-11-2017.
 */

public class MyService extends Service {
    private MediaPlayer mediaPlayer = MainActivity.mediaPlayer;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mediaPlayer.start();
        MainActivity.seekBar.setProgress(0);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        mediaPlayer.stop();
    }
}
