package com.example.servicesample;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.widget.Button;

import java.io.IOException;

public class SoundManageService extends Service {

    private MediaPlayer player;

    @Override
    public void onCreate() {
        this.player = new MediaPlayer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 音声ファイルのURI文字列の作成
        // ----------------------------------------------------------------------
        // アプリ内のリソース音声ファイルのURI文字列
        // 「android.resource://アプリのルートパッケージ/リソースファイルのR値」
        // ----------------------------------------------------------------------
        String mediaFileUriStr = "android.resource://" + getPackageName() + "/" + R.raw.sample;

        // 音声ファイルのURI文字列からURIオブジェクトの作成
        Uri mediaFileUri = Uri.parse(mediaFileUriStr);

        try {
            // メディアプレーヤーに音声ファイルを設定
            this.player.setDataSource(SoundManageService.this, mediaFileUri);

            // 非同期でのメディア再生準備完了時のリスナ設定
            this.player.setOnPreparedListener(new PlayerPreparedListener());

            // メディア再生終了時のリスナ設定
            this.player.setOnCompletionListener(new PlayerCompletionListener());

            // 非同期でのメディア再生準備
            this.player.prepareAsync();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {

        // 再生中であれば停止
        if (this.player.isPlaying()) {
            this.player.stop();
        }

        // プレーヤーを解放
        this.player.release();

        // nullを設定
        this.player = null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * プレーヤーの再生準備が整った時のリスナ
     */
    private class PlayerPreparedListener implements MediaPlayer.OnPreparedListener {

        @Override
        public void onPrepared(MediaPlayer mp) {
            mp.start();
        }
    }

    /**
     * 再生が終了した時のリスナ
     */
    private class PlayerCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            stopSelf();
        }
    }
}