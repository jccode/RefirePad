package com.hongdingltd.refirepad;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class InfoActivity extends AppCompatActivity {

    private static final String TAG = "InfoActivity";
    private static final int MSG_TYPE_COUNTDOWN = 191;
    private Handler handler;
    private Timer timer = new Timer();
    private TimerTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        final TextView textView = (TextView) findViewById(R.id.info);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Log.d(TAG, "Message coming, msg type:" + msg.what + "; payload:"+msg.arg1);
                switch (msg.what) {
                    case MSG_TYPE_COUNTDOWN:
                        int counter = msg.arg1;
                        if (counter < 0) {
                            timer.cancel();
                            goNext();
                        }
                        textView.setText(counter +" seconds left to leave!");
                        break;
                    default:
                        super.handleMessage(msg);
                }

            }
        };

        task = new TimerTask() {
            int count = 10;

            @Override
            public void run() {
                Log.d(TAG, "running, count = " + count);
                count = --count;
                Message message = handler.obtainMessage(MSG_TYPE_COUNTDOWN, count, 0);
                message.sendToTarget();
            }
        };

        timer.schedule(task, 1000 * 3, 1000 * 1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    private void goNext() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        finish();
    }
}
