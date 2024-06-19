package com.example.spda_app.threads;

import android.content.Context;
import android.media.MediaPlayer;

import com.example.spda_app.R;

public class PlayAlarmThread implements Runnable{
    int level = 1;
    boolean playing = false;
    Context context;
    public PlayAlarmThread(Context context) {
        this.context = context;
    }
    @Override
    public void run() {
        activateAlarm();
    }

    public void setAlarmLevel(int level) {
        this.level = level;
    }
    public int getAlarmLevel() {
        return level;
    }
    public boolean isPlaying() {return playing;}
    private void activateAlarm() {
        MediaPlayer player;
        if(level == 1) player = alarm_1();
        else if(level == 2) player = alarm_2();
        else if(level == 3) player = alarm_3();
        else player = alarm_1();

        player.start();
        playing = true;
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                player.release();
                playing = false;
            }
        });
    }
    private void activateAlarm(int level) {
        this.level = level;
        activateAlarm();
    }
    private MediaPlayer alarm_1() {
        return MediaPlayer.create(context, R.raw.song_1);
    }
    private MediaPlayer alarm_2() {
        return MediaPlayer.create(context, R.raw.song_2);
    }
    private MediaPlayer alarm_3() {
        return MediaPlayer.create(context, R.raw.song_3);
    }


}
