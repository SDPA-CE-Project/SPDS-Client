package com.example.spda_app;

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
    private boolean loaded = false;

    private int selectedMP3Index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // SongSettingActivity에서 선택된 음악의 인덱스를 가져옴
        selectedMP3Index = SongSettingActivity.getSelectedMP3Index();

        // SoundPool 초기화
        initSoundPool(selectedMP3Index);

        // 음악을 실행하는 로직
        playMusic();
    }

    // SoundPool 초기화 메서드
    private void initSoundPool(int selectedMP3Index) {
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

        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                loaded = true;
            }
        });

        switch (selectedMP3Index) {
            case 0:
                soundID = soundPool.load(this, R.raw.song_1, 1);
                break;
            case 1:
                soundID = soundPool.load(this, R.raw.song_2, 1);
                break;
            case 2:
                soundID = soundPool.load(this, R.raw.song_3, 1);
                break;
            default:
                break;
        }
    }


    // 선택된 음악을 재생하는 메서드
    private void playMusic() {
        if (loaded) {
            // SoundPool을 사용하여 음악 재생
            soundPool.play(soundID, 1, 1, 1, 0, 1);
        }
    }
}
