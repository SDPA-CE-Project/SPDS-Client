package com.example.spda_app;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.appcompat.app.AppCompatActivity;

public class PlayVibrate extends AppCompatActivity {

    private static Vibrator vibrator;
    private boolean isPlaying = false; // 재생 중인지 여부를 나타내는 변수
    private int selectedVibrationIndex;

    private Context context;

    public PlayVibrate(Context context) {
        this.context = context;
        initialize();
    }

    // Vibrator 초기화 메서드
    private void initialize() {
        if (vibrator == null) {
            vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        }
        selectedVibrationIndex = VibrationSettingActivity.getSelectedVibrationIndex();
    }

    // 선택된 진동 패턴을 재생하는 메서드
    public void playAlarm() {
        if (!isPlaying) { // 재생 중이 아닌 경우에만 재생
            vibrate(selectedVibrationIndex);
            isPlaying = true;
        }
    }

    private void vibrate(int index) {
        long[] pattern = getVibrationPattern(index);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            VibrationEffect vibrationEffect = VibrationEffect.createWaveform(pattern, -1);
            vibrator.vibrate(vibrationEffect);
        } else {
            vibrator.vibrate(pattern, -1);
        }
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void stopAlarm() {
        if (isPlaying) {
            vibrator.cancel();
            isPlaying = false;
        }
    }

    private long[] getVibrationPattern(int index) {
        switch (index) {
            case 0:
                return new long[]{0, 500, 250, 500}; // Pattern 1
            case 1:
                return new long[]{0, 300, 200, 300}; // Pattern 2
            case 2:
                return new long[]{0, 1000, 500, 1000}; // Pattern 3
            default:
                return new long[]{0}; // Default pattern
        }
    }
}