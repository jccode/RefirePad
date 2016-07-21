package com.hongdingltd.refirepad;

import android.content.res.Configuration;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;


public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";
    private Display currDisplay;
    private SurfaceView surfaceView;
    private MediaPlayer mediaPlayer;
    private SurfaceHolder holder;
    private MediaPlayerListener listener;

    private int width;
    private int height;
    private boolean isVideoReady;
    private boolean isVideoSizeKnow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestFullscreen();
        setContentView(R.layout.activity_main);

        surfaceView = (SurfaceView) findViewById(R.id.surface);
        holder = surfaceView.getHolder();
        holder.addCallback(new SurfaceCallback(this));
        listener = new MediaPlayerListener(this);
        currDisplay = this.getWindowManager().getDefaultDisplay();
    }

    private void requestFullscreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
        cleanUp();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseMediaPlayer();
        cleanUp();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.sizeChanged(mediaPlayer.getVideoWidth(), mediaPlayer.getVideoHeight());
    }

    private void surfaceReady() {
        this.playVideo();
    }

    private void sizeChanged(int width, int height) {
        this.isVideoSizeKnow = true;

        Point p = new Point();
        currDisplay.getSize(p);
//        System.out.println("size changed. width="+width+";height="+height+
//                "; display,width="+currDisplay.getWidth()+";height="+currDisplay.getHeight() +
//                "; display,width="+p.x+";height="+p.y);

        // scale
        float wRatio = (float)width / p.x;
        float hRatio = (float)height / p.y;
        float ratio = Math.max(wRatio, hRatio);

        this.width = (int)Math.ceil(width / ratio);
        this.height = (int)Math.ceil(height / ratio);

        holder.setFixedSize(this.width, this.height);
    }

    private void videoPrepared() {
        this.isVideoReady = true;
    }

    private void cleanUp() {
        this.width = 0;
        this.height = 0;
        this.isVideoReady = false;
        this.isVideoSizeKnow = false;
    }

    private void playVideo() {
        cleanUp();

        mediaPlayer =  MediaPlayer.create(this, R.raw.sample);
        mediaPlayer.setDisplay(holder);
        mediaPlayer.setOnBufferingUpdateListener(listener);
        mediaPlayer.setOnCompletionListener(listener);
        mediaPlayer.setOnPreparedListener(listener);
        mediaPlayer.setOnVideoSizeChangedListener(listener);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    private void startVideoPlayback() {
        if(isVideoReady && isVideoSizeKnow) {
            mediaPlayer.start();
        }
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    class MediaPlayerListener implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener,
            MediaPlayer.OnPreparedListener, MediaPlayer.OnVideoSizeChangedListener {

        private MainActivity context;

        public MediaPlayerListener(MainActivity context) {
            this.context = context;
        }

        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            Log.d(TAG, "onBufferingUpdate call");
        }

        @Override
        public void onCompletion(MediaPlayer mp) {
            Log.d(TAG, "onCompletion call");
            // repeat
            context.startVideoPlayback();
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            Log.d(TAG, "onPrepared call");
            context.videoPrepared();
            context.startVideoPlayback();
        }

        @Override
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
            Log.d(TAG, "onVideoSizeChanged call");
            context.sizeChanged(width, height);
            context.startVideoPlayback();
        }
    }


    class SurfaceCallback implements SurfaceHolder.Callback {

        private MainActivity context;

        public SurfaceCallback(MainActivity context) {
            this.context = context;
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Log.d(TAG, "surfaceCreated call");
            context.surfaceReady();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Log.d(TAG, "surfaceChanged call");
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.d(TAG, "surfaceDestroyed call");
        }
    }


}


