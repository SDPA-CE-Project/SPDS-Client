package com.example.spda_app;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;

import androidx.appcompat.app.AppCompatActivity;

public class PlayMedia extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false; // 재생 중인지 여부를 나타내는 변수 추가
    private int selectedMP3Index;
    private Context context;

    public PlayMedia(Context context) {
        this.context = context;
        initMediaPlayer();
    }

    // MediaPlayer 초기화 메서드
    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build());

        selectedMP3Index = SongSettingActivity.getSelectedMP3Index();

        switch (selectedMP3Index) {
            case 0:
                mediaPlayer = MediaPlayer.create(context, R.raw.song_1);
                break;
            case 1:
                mediaPlayer = MediaPlayer.create(context, R.raw.song_2);
                break;
            case 2:
                mediaPlayer = MediaPlayer.create(context, R.raw.song_3);
                break;
            default:
                break;
        }

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                onMusicCompletion();
            }
        });
    }

    // 선택된 음악을 재생하는 메서드
    public void playMusic() {
        if (!isPlaying) { // 재생 중이 아닌 경우에만 재생
            isPlaying = true;
            mediaPlayer.start();
        }
    }

    // 음악을 중지하는 메서드
    public void stopMusic() {
        if (isPlaying) { // 재생 중인 경우에만 중지
            mediaPlayer.pause(); // 재생 중지
            mediaPlayer.seekTo(0); // 시작 부분으로 재생 위치를 이동
            isPlaying = false; // 재생 상태 업데이트
        }
    }

    // 노래 재생 완료 시 호출되는 메서드
    public void onMusicCompletion() {
        isPlaying = false; // 재생 상태 업데이트
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    // Activity가 종료될 때 MediaPlayer 리소스 해제
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
