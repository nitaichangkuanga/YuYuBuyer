package com.wushuikeji.www.yuyubuyer.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.utils.SDUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class RecorderVideoActivity extends AppCompatActivity implements SurfaceHolder.Callback, MediaRecorder.OnInfoListener, MediaRecorder.OnErrorListener {

    @InjectView(R.id.rl_video_back)
    RelativeLayout rl_video_back;
    //切换录制
    @InjectView(R.id.ib_video_img)
    ImageButton ib_video_img;
    //VideoView
    @InjectView(R.id.vidio_videoView)
    VideoView vidio_videoView;
    //进度条
    @InjectView(R.id.video_pb)
    ProgressBar video_pb;
    //开始录制
    @InjectView(R.id.iv_video_start)
    ImageView iv_video_start;
    //文字
    @InjectView(R.id.tv_video_text)
    TextView tv_video_text;
    //停止
    @InjectView(R.id.iv_video_stop)
    ImageView iv_video_stop;
    //计时器
    @InjectView(R.id.chronometer)
    Chronometer chronometer;
    //开始录制的文字
    @InjectView(R.id.video_tv)
    TextView video_tv;

    private SurfaceHolder mSurfaceHolder;

    private PowerManager.WakeLock mWakeLock;

    private Camera mCamera;
    private int frontCamera = 1; // 0 is back camera，1 is front camera
    private MediaRecorder mediaRecorder;
    private int previewWidth = 480;
    private int previewHeight = 480;
    int defaultVideoFrameRate = -1;
    String localPath = "";// path to save recorded video
    private File mFile;
    private Intent mIntent;

    //进度条的行走
    private TimerTask task;
    private Timer timer;
    //记录进度时间
    private int progressTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorder_video);
        initView();
        initEvent();
    }

    private void initView() {
        ButterKnife.inject(this);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "RecordVideoActivity");
        mWakeLock.acquire();

        mIntent = getIntent();

        //设置回调
        mSurfaceHolder = vidio_videoView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    private void initEvent() {
        //取消
        rl_video_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back(null);
            }
        });
        //开始录制
        iv_video_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTranscribeVideo();
            }
        });
        //切换视频
        ib_video_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchCamera();
            }
        });
        //录制停止(完成)
        iv_video_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_video_stop.setEnabled(false);
                stopRecording();
                ib_video_img.setVisibility(View.VISIBLE);
                chronometer.stop();
                iv_video_start.setVisibility(View.VISIBLE);
                iv_video_stop.setVisibility(View.INVISIBLE);
               //ToastUtils.showToastInUIThread(RecorderVideoActivity.this,"录制完成");
                //关闭定时器
                if(timer != null) {
                    timer.cancel();
                    timer = null;
                }
                if(task != null) {
                    task.cancel();
                    task = null;
                }
                if(!TextUtils.isEmpty(localPath)) {
                    mIntent.putExtra("videoPath",localPath);
                    mIntent.putExtra("videoStatus",true);
                }
                setResult(RESULT_OK,mIntent);
                finish();
            }
        });
    }


    /**
     * 停止录制
     */
    public void stopRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.setOnErrorListener(null);
            mediaRecorder.setOnInfoListener(null);
            try {
                mediaRecorder.stop();
            } catch (Exception e) {
            }
        }
        releaseRecorder();

        if (mCamera != null) {
            mCamera.stopPreview();
            releaseCamera();
        }
    }

    /**
     * 释放录音的资源
     */
    private void releaseRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    /**
     * 释放录像的资源
     */
    protected void releaseCamera() {
        try {
            if (mCamera != null) {
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }
        } catch (Exception e) {
        }
    }

    @SuppressLint("NewApi")
    public void switchCamera() {

        if (mCamera == null) {
            return;
        }
        if (Camera.getNumberOfCameras() >= 2) {
            ib_video_img.setEnabled(false);

            if (mCamera != null) {
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }

            switch (frontCamera) {
                case 0:
                    //后摄像头
                    mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
                    frontCamera = 1;
                    break;
                case 1:
                    //前摄像头
                    mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
                    frontCamera = 0;
                    break;
            }
            try {
                mCamera.lock();
                mCamera.setDisplayOrientation(90);
                mCamera.setPreviewDisplay(vidio_videoView.getHolder());
                mCamera.startPreview();
            } catch (IOException e) {
                mCamera.release();
                mCamera = null;
            }
            ib_video_img.setEnabled(true);
        }
    }

    /**
     * 录制视频
     */
    private void startTranscribeVideo() {

        if(!startRecording())
            return;
        //ToastUtils.showToastInUIThread(RecorderVideoActivity.this,"录像开始");
        //图片更换
        ib_video_img.setVisibility(View.INVISIBLE);
        iv_video_start.setVisibility(View.INVISIBLE);
        iv_video_start.setEnabled(false);
        iv_video_stop.setVisibility(View.VISIBLE);
        tv_video_text.setVisibility(View.VISIBLE);
        //默认文字消失
        video_tv.setVisibility(View.INVISIBLE);
        //计时器显示
        chronometer.setVisibility(View.VISIBLE);
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
        //进度条走
        video_pb.setMax(420);
        progressBarDisplay();
    }

    public boolean startRecording(){
        if (mediaRecorder == null){
            if(!initRecorder())
                return false;
        }
        mediaRecorder.setOnInfoListener(this);
        mediaRecorder.setOnErrorListener(this);
        mediaRecorder.start();
        return true;
    }

    /**
     * 初始化录音
     * @return
     */
    @SuppressLint("NewApi")
    private boolean initRecorder(){

        if(!SDUtils.isSdcardExist()){
            showNoSDCardDialog();
            return false;
        }

        if (mCamera == null) {
            if(!initCamera()){
                showFailDialog();
                return false;
            }
        }
        vidio_videoView.setVisibility(View.VISIBLE);
        mCamera.stopPreview();
        mediaRecorder = new MediaRecorder();
        mCamera.unlock();
        mediaRecorder.setCamera(mCamera);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        if (frontCamera == 1) {
            mediaRecorder.setOrientationHint(270);
        } else {
            mediaRecorder.setOrientationHint(90);
        }

        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        // set resolution, should be set after the format and encoder was set
        mediaRecorder.setVideoSize(previewWidth, previewHeight);
        mediaRecorder.setVideoEncodingBitRate(384 * 1024);
        // set frame rate, should be set after the format and encoder was set
        if (defaultVideoFrameRate != -1) {
            mediaRecorder.setVideoFrameRate(defaultVideoFrameRate);
        }
        // set the path for video file
        //SD/video/video.mp4
        if(mFile == null){
            mFile = new File(Environment.getExternalStorageDirectory(),"video");
        }

        if(!mFile.exists()) {
            mFile.mkdir();
        }
//        localPath = mFile.getAbsolutePath() + File.separator + "video.mp4";
        localPath = mFile.getAbsolutePath() + File.separator + "video.mp4";
        mediaRecorder.setOutputFile(localPath);
        //控制视频的时间
        mediaRecorder.setMaxDuration(420000);
        mediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    /**
     * 初始化照相机
     */
    @SuppressLint("NewApi")
    private boolean initCamera() {
        try {
            if (frontCamera == 0) {
                mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
            } else {
                mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
            }
            Camera.Parameters camParams = mCamera.getParameters();
            mCamera.lock();
            mSurfaceHolder = vidio_videoView.getHolder();
            mSurfaceHolder.addCallback(this);
            mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            //设置
            mCamera.setDisplayOrientation(90);

        } catch (RuntimeException ex) {
            return false;
        }
        return true;
    }
    /**
     * SurfaceHolder的回调
     * @param holder
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (mCamera == null){
            if(!initCamera()){
                showFailDialog();
                return;
            }

        }
        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
            mCamera.startPreview();
            handleSurfaceChanged();
        } catch (Exception e1) {
            showFailDialog();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mSurfaceHolder = holder;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private void handleSurfaceChanged() {
        if (mCamera == null) {
            finish();
            return;
        }
        boolean hasSupportRate = false;
        List<Integer> supportedPreviewFrameRates = mCamera.getParameters().getSupportedPreviewFrameRates();
        if (supportedPreviewFrameRates != null
                && supportedPreviewFrameRates.size() > 0) {
            Collections.sort(supportedPreviewFrameRates);
            for (int i = 0; i < supportedPreviewFrameRates.size(); i++) {
                int supportRate = supportedPreviewFrameRates.get(i);

                if (supportRate == 15) {
                    hasSupportRate = true;
                }

            }
            if (hasSupportRate) {
                defaultVideoFrameRate = 15;
            } else {
                defaultVideoFrameRate = supportedPreviewFrameRates.get(0);
            }

        }
        // get all resolutions which camera provide
        List<Camera.Size> resolutionList = mCamera.getParameters().getSupportedPreviewSizes();
        if (resolutionList != null && resolutionList.size() > 0) {
            Collections.sort(resolutionList, new ResolutionComparator());
            Camera.Size previewSize = null;
            boolean hasSize = false;

            // use 60*480 if camera support
            for (int i = 0; i < resolutionList.size(); i++) {
                Camera.Size size = resolutionList.get(i);
                if (size != null && size.width == 640 && size.height == 480) {
                    previewSize = size;
                    previewWidth = previewSize.width;
                    previewHeight = previewSize.height;
                    hasSize = true;
                    break;
                }
            }
            // use medium resolution if camera don't support the above resolution
            if (!hasSize) {
                int mediumResolution = resolutionList.size() / 2;
                if (mediumResolution >= resolutionList.size())
                    mediumResolution = resolutionList.size() - 1;
                previewSize = resolutionList.get(mediumResolution);
                previewWidth = previewSize.width;
                previewHeight = previewSize.height;

            }
        }
    }
    class ResolutionComparator implements Comparator<Camera.Size> {

        @Override
        public int compare(Camera.Size lhs, Camera.Size rhs) {
            if(lhs.height!=rhs.height)
                return lhs.height-rhs.height;
            else
                return lhs.width-rhs.width;
        }

    }


    /**
     * 弹出没有SD卡的Dialog
     */
    private void showNoSDCardDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.prompt)
                .setMessage(R.string.mess)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                finish();
                            }
                        }).setCancelable(false).show();
    }

    private void showFailDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.prompt)
                .setMessage("打开设备失败")
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                finish();

                            }
                        }).setCancelable(false).show();

    }

    /**
     * 录音的监听
     */
    @Override
    public void onInfo(MediaRecorder mr, int what, int extra) {
        if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
            stopRecording();
            ib_video_img.setVisibility(View.VISIBLE);
            chronometer.stop();
            iv_video_start.setVisibility(View.VISIBLE);
            iv_video_stop.setVisibility(View.INVISIBLE);
            chronometer.stop();
            if (localPath == null) {
                return;
            }
            //60秒到了，返回前一个Activity
            if(!TextUtils.isEmpty(localPath)) {
                mIntent.putExtra("videoPath",localPath);
                mIntent.putExtra("videoStatus",true);
            }
            setResult(RESULT_OK,mIntent);
            finish();
        }
    }

    /**
     * 录音错误的监听
     */
    @Override
    public void onError(MediaRecorder mr, int what, int extra) {
        stopRecording();
        Toast.makeText(this,
                "录音发生错误，停止录制",
                Toast.LENGTH_SHORT).show();
        //关闭定时器
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
        if(task != null) {
            task.cancel();
            task = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mWakeLock == null) {
            // keep screen on
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK,
                    "RecordVideoActivity");
            mWakeLock.acquire();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mWakeLock != null) {
            mWakeLock.release();
            mWakeLock = null;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseCamera();

        if (mWakeLock != null) {
            mWakeLock.release();
            mWakeLock = null;
        }
        //关闭定时器
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
        if(task != null) {
            task.cancel();
            task = null;
        }
    }

    @Override
    public void onBackPressed() {
        back(null);
    }

    public void back(View view) {
        releaseRecorder();
        releaseCamera();
        finish();
    }

    /**
     * 计时器，用于给进度条
     */
    public void progressBarDisplay() {
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                // 子线程,进度条可以更新UI
                progressTime ++;
                video_pb.setProgress(progressTime);
            }
        };
        timer.schedule(task, 0, 1000);
    }
}
