package com.example.spda_app;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class PlaySong extends AppCompatActivity {
    private SoundPool soundPool;
    private int soundID;
    private boolean isPlaying = false; // 재생 중인지 여부를 나타내는 변수 추가
    private int selectedIndex;
    private int streamId = -1;

    private Context context;

    public PlaySong(Context context) {
        this.context = context;
        initSoundPool();
    }

    // SoundPool 초기화 메서드
    private void initSoundPool() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(1)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        }

        selectedIndex = AlarmbellSettingActivity.getSelectedIndex();

        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                onMusicCompletion();
            }
        });

        switch (selectedIndex) {
            case 0:
                soundID = soundPool.load(context, R.raw.bell1, 1);
                break;
            case 1:
                soundID = soundPool.load(context, R.raw.bell2, 1);
                break;
            case 2:
                soundID = soundPool.load(context, R.raw.bell3, 1);
                break;
            default:
                break;
        }
    }
    // 선택된 음악을 재생하는 메서드
    public void playAlarm() {
        if (!isPlaying) { // 재생 중이 아닌 경우에만 재생
            streamId = soundPool.play(soundID, 1.0f, 1.0f, 1, -1, 1.0f);
            isPlaying = true;
        }
    }

    // 노래 재생 완료 시 호출되는 메서드
    public void onMusicCompletion() {
        isPlaying = false; // 재생 상태 업데이트
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void stopAlarm() {
        if (isPlaying) {
            soundPool.stop(streamId);
            streamId = soundPool.play(soundID, 1.0f, 1.0f, 1, 0, 1.0f); // 초기 상태로 돌리기 위해 다시 재생
            soundPool.stop(streamId); // 바로 중지하여 재생 위치를 초기 상태로
            isPlaying = false;
        }
    }
}
