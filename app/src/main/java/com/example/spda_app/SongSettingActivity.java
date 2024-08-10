package com.example.spda_app;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.spda_app.databinding.SongSettingBinding;

import java.io.IOException;
import java.util.ArrayList;


public class SongSettingActivity extends AppCompatActivity {

    private static int selectedMP3Index = 0;
    private ListView listViewMP3;
    private ArrayList<String> mp3List;

    private SongSettingBinding binding;
    private PlayMedia playmedia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SongSettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.number.setText(String.valueOf(getSelectedMP3Index()));

        // \음악 리스트 초기화
        mp3List = new ArrayList<>();
        mp3List.add("Song 1");
        mp3List.add("Song 2");
        mp3List.add("Song 3");

        playmedia = new PlayMedia(this);

        // 리스트뷰 설정 및 어댑터 설정...
        listViewMP3 = binding.listViewMP3;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, mp3List);
        listViewMP3.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listViewMP3.setAdapter(adapter);

        // 리스트뷰 아이템 클릭 리스너 설정
        listViewMP3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 클릭한 아이템의 인덱스 값을 selectedMP3Index에 저장
                setSelectedMP3Index(position);
                binding.number.setText(String.valueOf(getSelectedMP3Index()));
                if (playmedia.isPlaying()) {
<<<<<<< HEAD
                    playmedia.stopAlarm();
                    playmedia = new PlayMedia(SongSettingActivity.this);
                    playmedia.playAlarm();
                }
                playmedia.playAlarm();
=======
                    playmedia.stopMusic();
                    playmedia = new PlayMedia(SongSettingActivity.this);
                    playmedia.playMusic();
                }
                playmedia.playMusic();
>>>>>>> 5d9da813ce299e1596afbea645f63d3641d18c07
            }
        });
        Button btnConfirm = binding.confirm;
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 확인 버튼을 클릭했을 때 이전 화면으로 돌아가는 로직을 추가
<<<<<<< HEAD
                playmedia.stopAlarm();
=======
                playmedia.stopMusic();
>>>>>>> 5d9da813ce299e1596afbea645f63d3641d18c07
                finish();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    // 인덱스 번호 설정 메서드
    public static void setSelectedMP3Index(int index) {
        selectedMP3Index = index;
    }

    // 현재 설정된 인덱스 번호 반환 메서드
    public static int getSelectedMP3Index() {
        return selectedMP3Index;
    }

}

