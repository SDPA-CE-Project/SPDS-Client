package com.example.spda_app;

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

    private SongSettingBinding binding2;
    ListView listViewMP3;
    Button btnPlay, btnStop;
    TextView tvMP3;
    ProgressBar pbMP3;
    ArrayList<String> mp3List;
    MediaPlayer mPlayer;
    String selectedMP3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding2 = SongSettingBinding.inflate(getLayoutInflater()); // 체크할것
        setContentView(binding2.getRoot());
        setTitle("실험제목");

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mp3List = new ArrayList<String>();
        mp3List.add(String.valueOf(R.raw.song_1));
        mp3List.add(String.valueOf(R.raw.song_2));
        mp3List.add(String.valueOf(R.raw.song_3));

        // 리스트뷰에 mp3 음악 추가하기
        listViewMP3 = binding2.listViewMP3;
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_single_choice, mp3List);
        listViewMP3.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listViewMP3.setAdapter(adapter);
        listViewMP3.setItemChecked(0, true);

        listViewMP3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedMP3 = mp3List.get(i);
            }
        });
        selectedMP3 = mp3List.get(0);

        btnPlay = binding2.btnPlay;
        btnStop = binding2.btnStop;
        tvMP3 = binding2.tvMP3;
        pbMP3 = binding2.pbMP3;

        // 재생버튼
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    mPlayer = new MediaPlayer();
                    mPlayer.setDataSource(getApplicationContext(), Uri.parse("android.resource://" + getPackageName() + "/" + selectedMP3));
                    mPlayer.prepare();
                    mPlayer.start();
                    btnPlay.setClickable(false);
                    btnStop.setClickable(true);
                    tvMP3.setText("실행중인 음악:" + selectedMP3);
                    pbMP3.setVisibility(View.VISIBLE);
                }catch (IOException e){}
            }
        });

        // 정지 버튼
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPlayer.start();
                mPlayer.reset();
                btnPlay.setClickable(true);
                btnStop.setClickable(false);
                tvMP3.setText("실행중인 음악: ");
                pbMP3.setVisibility(View.INVISIBLE);
            }
        });

        btnStop.setClickable(false);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

//    액션바를 이용한 방법
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == android.R.id.home) {
//            onBackPressed(); // 기본 뒤로가기 동작 실행
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
}
