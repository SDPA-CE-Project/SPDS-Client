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
    private boolean loaded = false;
    private boolean isPlaying = false; // 재생 중인지 여부를 나타내는 변수 추가
    private int selectedMP3Index;

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

        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                loaded = true;
                onMusicCompletion();
            }
        });

        selectedMP3Index = SongSettingActivity.getSelectedMP3Index();

        switch (selectedMP3Index) {
            case 0:
                soundID = soundPool.load(context, R.raw.song_1, 1);
                break;
            case 1:
                soundID = soundPool.load(context, R.raw.song_2, 1);
                break;
            case 2:
                soundID = soundPool.load(context, R.raw.song_3, 1);
                break;
            default:
                break;
        }
    }
    // 선택된 음악을 재생하는 메서드
    public void playMusic() {
        if (loaded && !isPlaying) { // 재생 중이 아닌 경우에만 재생
            isPlaying = true;
            soundPool.play(soundID, 10, 10, 1, 0, 1);
        }
    }

    // 노래 재생 완료 시 호출되는 메서드
    public void onMusicCompletion() {
        isPlaying = false; // 재생 상태 업데이트
    }

    public boolean isPlaying() {
        return isPlaying;
    }
}
