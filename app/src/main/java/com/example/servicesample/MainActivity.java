package com.example.servicesample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * 再生ボタン押下時の処理
     * @param view
     */
    public void onPlayButtonClick(View view) {

        // インテント作成
        Intent intent = new Intent(MainActivity.this, SoundManageService.class);

        // サービス開始
        startService(intent);

        // 開始と終了ボタンの押下制御
        Button btPlay = findViewById(R.id.btPlay);
        Button btStop = findViewById(R.id.btStop);
        btPlay.setEnabled(false);
        btStop.setEnabled(true);
    }

    /**
     * 停止ボタン押下時の処理
     * @param view
     */
    public void onStopButtonClick(View view) {

        // インテント作成
        Intent intent = new Intent(MainActivity.this, SoundManageService.class);

        // サービス停止
        stopService(intent);

        // 開始と終了ボタンの押下制御
        Button btPlay = findViewById(R.id.btPlay);
        Button btStop = findViewById(R.id.btStop);
        btPlay.setEnabled(true);
        btStop.setEnabled(false);
    }
}