package com.wushuikeji.www.yuyubuyer.activity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.MediaController;
import android.widget.VideoView;

import com.wushuikeji.www.yuyubuyer.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PlayVideoActivity extends AppCompatActivity {

    @InjectView(R.id.videoView)
    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        //getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.activity_play_video);
        ButterKnife.inject(this);
    }

    private void initData() {
        //得到url
        String videoUrl = getIntent().getStringExtra("url");

        //设置视频控制器
        videoView.setMediaController(new MediaController(this));

        //播放完成回调
        videoView.setOnCompletionListener(new MyPlayerOnCompletionListener());

        if(!TextUtils.isEmpty(videoUrl)) {
            //设置视频路径
            videoView.setVideoURI(Uri.parse(videoUrl));
            //开始播放视频
            videoView.start();
        }
    }

    private void initEvent() {

    }

    class MyPlayerOnCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            //Toast.makeText( LocalVideoActivity.this, "播放完成了", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(videoView != null) {
            videoView.pause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(videoView != null) {
            videoView.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(videoView != null && !videoView.isPlaying()) {
            videoView.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(videoView != null) {
            videoView.stopPlayback();
        }
    }
}
