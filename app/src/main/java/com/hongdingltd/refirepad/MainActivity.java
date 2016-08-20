package com.hongdingltd.refirepad;

import android.content.Intent;
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
    private int videoPosition;
    private boolean isVideoReady;
    private boolean isVideoSizeKnow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate called!");

        super.onCreate(savedInstanceState);
        requestFullscreen();
        setContentView(R.layout.activity_main);

        surfaceView = (SurfaceView) findViewById(R.id.surface);
        holder = surfaceView.getHolder();
        holder.addCallback(new SurfaceCallback(this));
        listener = new MediaPlayerListener(this);
        currDisplay = this.getWindowManager().getDefaultDisplay();

        if(savedInstanceState != null) {
            int position = savedInstanceState.getInt("position");
            videoPosition = position;
            Log.d(TAG, "onCreate postion: "+position);
        }
    }

    private void requestFullscreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy called!");
        super.onDestroy();
        releaseMediaPlayer();
        cleanUp();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause called!");
        super.onPause();
        releaseMediaPlayer();
        cleanUp();

        saveCurrentPosition();
    }

//    @Override
//    protected void onResume() {
//        Log.d(TAG, "onResume called!");
//        super.onResume();
//        if(mediaPlayer != null) {
//            startVideoPlayback();
//        }
//    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(mediaPlayer != null) {
            this.sizeChanged(mediaPlayer.getVideoWidth(), mediaPlayer.getVideoHeight());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        saveCurrentPosition();
        outState.putInt("position", this.videoPosition);
        super.onSaveInstanceState(outState);
    }

    private void saveCurrentPosition() {
        if (mediaPlayer != null) {
            this.videoPosition = mediaPlayer.getCurrentPosition();
        }
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

        mediaPlayer =  MediaPlayer.create(this, R.raw.refire_introduce);
        mediaPlayer.setDisplay(holder);
        mediaPlayer.setOnBufferingUpdateListener(listener);
        mediaPlayer.setOnCompletionListener(listener);
        mediaPlayer.setOnPreparedListener(listener);
        mediaPlayer.setOnVideoSizeChangedListener(listener);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    private void startVideoPlayback() {
        Log.d(TAG, "startVideoPlayback. position"+videoPosition );
        if(isVideoReady && isVideoSizeKnow) {
            if(videoPosition != 0) {
                mediaPlayer.seekTo(videoPosition);
            }
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
            //context.startVideoPlayback();

            gotoInfo();
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

        private void gotoInfo() {
            Intent intent = new Intent(context, InfoActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
            finish();
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


