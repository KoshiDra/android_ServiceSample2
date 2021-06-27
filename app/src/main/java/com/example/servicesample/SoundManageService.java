package com.example.servicesample;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.widget.Button;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.IOException;

public class SoundManageService extends Service {

    private MediaPlayer player;

    private static final String CHANNEL_ID = "sound_manager_service_notification_channel";

    @Override
    public void onCreate() {

        this.player = new MediaPlayer();

        // ----------------------------------------------------------------------------------------------
        // 通知チャネルの作成
        // ----------------------------------------------------------------------------------------------
        // 通知チャネル名取得（string.xml）
        String name = getString(R.string.notification_channel_name);

        // 通知チャネルの重要度設定（レベル：標準）
        int importance = NotificationManager.IMPORTANCE_DEFAULT;

        // 通知チャネル生成
        NotificationChannel channel = new NotificationChannel(this.CHANNEL_ID, name, importance);

        // Notificationオブジェクト取得（引数には取得したいサービスオブジェクトのclassを渡す）
        NotificationManager manager = getSystemService(NotificationManager.class);

        // 通知チャネル設定（NotificationManagerにNotificationChannelを登録）
        manager.createNotificationChannel(channel);
        // ----------------------------------------------------------------------------------------------
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
            Log.d("TEST", "PlayerPreparedListener実行");
            mp.start();

            // ----------------------------------------------------------------------------------------------
            // 再生処理開始通知
            // ----------------------------------------------------------------------------------------------

            // Notificationを作成するBuilderクラス生成
            NotificationCompat.Builder builder = new NotificationCompat.Builder(SoundManageService.this, CHANNEL_ID);

            // ** Builderへの設定 **
            // 通知エリアの表示アイコン設定
            builder.setSmallIcon(android.R.drawable.ic_dialog_info);
            // 通知ドロワーのタイトル設定
            builder.setContentTitle(getString(R.string.msg_notification_title_start));
            // 通知ドロワーのメッセージ設定
            builder.setContentText(getText(R.string.msg_notification_text_start));


            // 起動先Activityクラスを指定したIntentオブジェクト生成
            Intent intent = new Intent(SoundManageService.this, MainActivity.class);

            // 引継ぎデータを設定
            intent.putExtra("fromNotification", true);

            // PendingIntent（指定されたタイミングで起動するインテント）オブジェクト生成
            PendingIntent stopServiceIntent = PendingIntent.getActivity(SoundManageService.this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

            // PendingIntentをbuilderに設定
            builder.setContentIntent(stopServiceIntent);

            // タップされた通知メッセージを自動的に消去するように設定
            builder.setAutoCancel(true);

            // builderからNotificationオブジェクトを生成
            Notification notification = builder.build();

            // Notificationオブジェクトを元にサービスをフォアグラウンド化
            startForeground(200, notification);
        }
    }

    /**
     * 再生が終了した時のリスナ
     */
    private class PlayerCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {

            Log.d("TEST", "PlayerCompletionListener実行");

            // ----------------------------------------------------------------------------------------------
            // 再生処理終了通知
            // ----------------------------------------------------------------------------------------------

            // Notificationを作成するBuilderクラス生成
            NotificationCompat.Builder builder = new NotificationCompat.Builder(SoundManageService.this, CHANNEL_ID);

            // ** Builderへの設定 **
            // 通知エリアの表示アイコン設定
            builder.setSmallIcon(android.R.drawable.ic_dialog_info);
            // 通知ドロワーのタイトル設定
            builder.setContentTitle(getString(R.string.msg_notification_title_finish));
            // 通知ドロワーのメッセージ設定
            builder.setContentText(getText(R.string.msg_notification_text_finish));


            // BuilderからNotificationオブジェクト生成
            Notification notification = builder.build();

            // NotificationManagerCompactオブジェクト生成
            NotificationManagerCompat manager = NotificationManagerCompat.from(SoundManageService.this);

            // 通知実行（Notificationオブジェクトの表示）
            //---------------------------------------------------------------------------------------------------------
            // 第一引数：Builderから生成したNotificationオブジェクトを識別する数値（アプリ内で一意の値）
            // 第二引数：Builderから生成したNotificationオブジェクト
            //---------------------------------------------------------------------------------------------------------
            manager.notify(100, notification);

            stopSelf();
        }
    }
}