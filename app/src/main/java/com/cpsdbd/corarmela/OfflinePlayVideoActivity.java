package com.cpsdbd.corarmela;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.MediaController;
import android.widget.VideoView;

import static com.cpsdbd.corarmela.R.id.video_view;

public class OfflinePlayVideoActivity extends Activity {
    private VideoView videoView;
    private String videoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_play_video);

        videoPath = getIntent().getStringExtra("VIDEO_PATH");

        MediaController mediaController = new MediaController(this);

        videoView = (VideoView) findViewById(video_view);
        videoView.setMediaController(mediaController);

        if(videoPath!=null){
            videoView.setVideoPath(videoPath);
            setFullScreen();
            videoView.start();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this,MyBackgroundService.class);
        stopService(intent);
    }

    private void setFullScreen(){
        DisplayMetrics metrics = new DisplayMetrics(); getWindowManager().getDefaultDisplay().getMetrics(metrics);
        android.widget.RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) videoView.getLayoutParams();
        params.width =  metrics.widthPixels;
        params.height = metrics.heightPixels;
        params.leftMargin = 0;
        videoView.setLayoutParams(params);
    }
}
