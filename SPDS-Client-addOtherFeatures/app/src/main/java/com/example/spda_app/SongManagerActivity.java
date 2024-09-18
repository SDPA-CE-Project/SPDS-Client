package com.example.spda_app;

import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class SongManagerActivity extends AppCompatActivity {
    private ArrayList<Integer> soundIds;
    private SoundPool soundPool;
    private int selectedSoundIndex = 0;
    private boolean isPlayingAudio = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // SoundPool 초기화
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);

        // 효과음 로드
        soundIds = new ArrayList<>();
        soundIds.add(soundPool.load(this, R.raw.song_1, 1));
        soundIds.add(soundPool.load(this, R.raw.song_2, 1));
        soundIds.add(soundPool.load(this, R.raw.song_3, 1));

        // 선택된 음악 인덱스 설정
        Intent intent = getIntent();
        if (intent != null) {
            selectedSoundIndex = intent.getIntExtra("selectedSoundIndex", 0);
        }

        // 음악 재생
        playSelectedSound();
    }

    // 선택된 음악을 재생하는 메서드
    private void playSelectedSound() {
        if (!isPlayingAudio) {
            isPlayingAudio = true;
            // 선택된 음악 인덱스에 해당하는 음악 재생
            playSound(selectedSoundIndex);
        }
    }

    // 선택된 음악을 재생하는 메서드
    private void playSound(int index) {
        soundPool.play(soundIds.get(index), 1, 1, 1, 0, 1);
        // 재생 중인 음악 정보 설정 등 추가 작업 수행...
    }

    // 선택된 음악 인덱스 설정 메서드
    public void setSelectedSoundIndex(int index) {
        selectedSoundIndex = index;
    }

    // 다른 음악 관련 메서드들...
}

