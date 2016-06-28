package com.example.takeuchi.newas;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;

import org.java_websocket.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.NotYetConnectedException;
import java.util.stream.IntStream;

public class MainActivity extends AppCompatActivity implements Runnable {
    // 定数的な宣言
    private static final String LOGTAG = "TEST_LOG";
//    private Handler mHandler;
    private WebSocketClient mClient;


private boolean running = true;

    public void run(){
        // ソケット準備してみる
        try {
            //for(int i = 1; i <= 5; i++) {
            while(running) {
                Log.d(LOGTAG, "####### 無駄ログ #######");

                // 接続可能ならば状態をとってみる
                Thread.sleep(3000);
                Log.d(LOGTAG, "!!!ソケットtry開始!!!");
                URI uri = new URI("ws://127.0.0.1:9090/jsonrpc");
//Log.d(LOGTAG, "");



                mClient = new WebSocketClient(uri) {
                    @Override
                    public void onOpen(ServerHandshake serverHandshake) {
                        Log.d(LOGTAG, "onOpen");
                        //polling = true;
                        running = false;
                    }

                    @Override
//                public void onMessage(String s) {
                    public void onMessage(String message) {
                        Log.d(LOGTAG, "onMessage");
                        Log.d(LOGTAG, message);
                        // ここで再生停止[Player.OnStop]が取れたらKODIを止める必要がある
                        try {
                            JSONObject json = new JSONObject(message);
                            String methoddata = json.getString("method");
                            Log.d(LOGTAG, "["+methoddata+"]");
                            // 止まったと判断する応答文字列
                            String STR = "Player.OnStop";
                            if(methoddata.equals(STR)){
                                Log.d(LOGTAG, "プレーヤーが止まった模様だぞ");
                                // KODIを停止させる
                                mClient.send("{\"jsonrpc\":\"2.0\",\"method\":\"Application.Quit\",\"id\":1}");
                            }
                        }catch (JSONException e){
                            // 基本的にここは無視(エラー出続ける)
                            Log.d(LOGTAG, "JSONでエラーこいた");
                        }catch (InterruptedException e){
                            Log.d(LOGTAG, "JSONでエラーこいた");
                        }
                    }

                    @Override
                    public void onClose(int i, String s, boolean b) {
                        Log.d(LOGTAG, "onClose");
                    }

                    @Override
                    public void onError(Exception ex) {
                        Log.d(LOGTAG, "onError");
                        ex.printStackTrace();
                    }
                };

                // コネクションしてみる
                mClient.connect();
            }
        } catch (URISyntaxException e){
            Log.d(LOGTAG, "エラー[URISyntaxException]");
        } catch (InterruptedException e){

        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //mHandler = new Handler();

        // ここで部品の追加宣言が必要
        final Button buttonA = (Button)findViewById(R.id.buttonA);
        buttonA.setOnClickListener(buttonClick);
        final Button buttonB = (Button)findViewById(R.id.buttonB);
        buttonB.setOnClickListener(buttonClick);
        final Button buttonWS = (Button)findViewById(R.id.buttonWS);
        buttonWS.setOnClickListener(buttonClick);

/* 保険として残す
        // ソケット準備してみる
        try {
            Log.d(LOGTAG, "ソケットtry開始！");
            URI uri = new URI("ws://127.0.0.1:9090/jsonrpc");
//Log.d(LOGTAG, "");
            mClient = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    Log.d(LOGTAG, "onOpen");
                }

                @Override
//                public void onMessage(String s) {
                public void onMessage(String message) {
                    Log.d(LOGTAG, "onMessage");
                    Log.d(LOGTAG, message);
                }

                @Override
                public void onClose(int i, String s, boolean b) {
                    Log.d(LOGTAG, "onClose");
                }

                @Override
                public void onError(Exception ex) {
                    Log.d(LOGTAG, "onError");
                    ex.printStackTrace();
                }
            };

            // コネクションしてみる
            mClient.connect();

        } catch (URISyntaxException e){
            Log.d(LOGTAG, "エラー[URISyntaxException]");
        }
*/
    }

 //   TextView testSetting = (TextView) findViewById(R.id.textarea);

    private View.OnClickListener buttonClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            TextView testSetting = (TextView) findViewById(R.id.textarea);
            switch (view.getId()){
                case R.id.buttonA:
                    Log.d(LOGTAG, "Aボタン");
                    testSetting.setText("aaaaaaaaa");
                    break;
                case R.id.buttonB:
                    Log.d(LOGTAG, "Bボタン");
                    testSetting.setText("bbbbbbbbbb");
                    break;
                case R.id.buttonWS:
                    Log.d(LOGTAG, "WSボタン");
                    try{
                        // 送信
                        Log.d(LOGTAG, "送信");

                        // インテント方式を行ってみる
                        Uri uri = Uri.parse("http://192.168.222.186/adimg/1.mp4");
                        Intent intent1 = new Intent();
                        intent1.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent1.setClassName("org.xbmc.kodi", "org.xbmc.kodi.Splash");
                        intent1.setDataAndType(uri, "video/mp4");

                        //startActivityForResult(intent1, 1000);
                        startActivity(intent1);
                        Log.d(LOGTAG, "インテント完了");

                        // ここからKODIが動画再生を行っているかの確認をし、終了していたら落とすスレッドを作成する
                       // RunnableSample thread = new RunnableSample();
                        MainActivity MActivity = new MainActivity();
                        Thread t1 = new Thread(MActivity);
                        t1.start();

                    } catch(NotYetConnectedException e) {
                        Log.d(LOGTAG, "エラー[NotYetConnectedException]");
//                    } catch (InterruptedException e){
  //                      Log.d(LOGTAG, "エラー[InterruptedException]");
                    }

                    break;
            }
        }
    };

    // 戻りを求めてみる
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.d(LOGTAG, "----------------");
        Log.d(LOGTAG, Integer.toString(requestCode)); // 渡した値が来る
        Log.d(LOGTAG, Integer.toString(resultCode)); // KODIをいじれないので0が来る
        Log.d(LOGTAG, "================");
        // 返り値の取得
        //if (requestCode == MY_INTENT_BROWSER) {
        if (resultCode == RESULT_OK) {
            // Success
            Log.d(LOGTAG, "success");
        } else if (resultCode == RESULT_CANCELED) {
            // Handle cancel
            Log.d(LOGTAG, "canceled");

        }
    }



}


class RunnableSample {
    static class MyRunnable implements Runnable {
        @Override
        public void run() {
            Log.d("TEST_LOG", "TESTTTTTTTT");
        }
    }
    public static void main(String[] args) {
        Thread thread = new Thread(new MyRunnable());
        thread.start();
    }
}

