package com.wushuikeji.www.yuyubuyer.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.constants.Constants;
import com.wushuikeji.www.yuyubuyer.constants.MyConstants;
import com.wushuikeji.www.yuyubuyer.jsonparse.ContentParse;
import com.wushuikeji.www.yuyubuyer.jsonparse.SendAuthoCodeParse;
import com.wushuikeji.www.yuyubuyer.utils.BackListenerUtils;
import com.wushuikeji.www.yuyubuyer.utils.BitmapUtils;
import com.wushuikeji.www.yuyubuyer.utils.ISNetworkConnectUtils;
import com.wushuikeji.www.yuyubuyer.utils.RecoverClickUtils;
import com.wushuikeji.www.yuyubuyer.utils.RequestResponseUtils;
import com.wushuikeji.www.yuyubuyer.utils.SpUtils;
import com.wushuikeji.www.yuyubuyer.utils.ToastUtils;
import com.wushuikeji.www.yuyubuyer.utils.UIUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;

public class AddDynamicActivity extends AppCompatActivity {

    @InjectView(R.id.rl_add_back)
    RelativeLayout rl_add_back;
    //发送
    @InjectView(R.id.rl_addDynamic_send)
    RelativeLayout rl_addDynamic_send;

    @InjectView(R.id.et_add_input)
    EditText et_add_input;

    //视频
    @InjectView(R.id.ib_add_video)
    ImageButton ib_add_video;

    //放视频的缩放图
    @InjectView(R.id.add_img)
    ImageView add_img;

    @InjectView(R.id.pb_addLoading_progressBar)
    ProgressBar pb_addLoading_progressBar;

    //删除视频图标
    @InjectView(R.id.add_delete)
    ImageButton add_delete;

    //视频的缩约图
    private Bitmap bitmap;

    //上传视频
    private String upLoadVideoUrl = Constants.commontUrl + "trend/upload_video";

    //发送动态
    private String sendDynamicUrl = Constants.commontUrl + "trend/add";

    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dynamic);
        initView();
        initEvent();
    }

    private void initView() {
        ButterKnife.inject(this);
        //得到用户ID
        mUserId = SpUtils.getCacheString(MyConstants.LOGINSPNAME, UIUtils.getContext(), MyConstants.BUYERID, "");
    }

    private void initEvent() {
        //返回
        BackListenerUtils.backFinish(rl_add_back,this);
        //视频
        ib_add_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //开启视频的拍摄
                Intent intent=new Intent();
                intent.setClass(AddDynamicActivity.this, RecorderVideoActivity.class);
                startActivityForResult(intent, 100);
            }
        });
        //发送
        rl_addDynamic_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //删除目录(取消发送)
                //    if(localPath != null){
                //        File file = new File(localPath);
                //        if(file.exists())
                //            file.delete();
                //    }
                //判断网络
                boolean iSNetworkConnect = ISNetworkConnectUtils.iSNetworkConnect(UIUtils.getContext());
                if (!iSNetworkConnect) {
                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "您尚未开启网络");
                    return;
                }else if(TextUtils.isEmpty(et_add_input.getText().toString().trim())) {
                    ToastUtils.showToastInUIThread(AddDynamicActivity.this,"动态文字不能为空");
                    return;
                }else if(bitmap == null) {
                    ToastUtils.showToastInUIThread(AddDynamicActivity.this,"视频不能为空");
                    return;
                }else {
                    //先上传视频
                    requestUpLoadPic();
                }
            }
        });
        //删除视频图标
        add_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_img.setVisibility(View.GONE);
                add_delete.setVisibility(View.GONE);
                if(ib_add_video !=null) {
                    ib_add_video.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    /**
     * 请求上传视频
     */
    private void requestUpLoadPic() {
        //需要先得到路径
        File videoFile = getVideoFile();
        if(videoFile != null) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", mUserId);
            //进度
            pb_addLoading_progressBar.setVisibility(View.VISIBLE);

            OkHttpUtils.post()
                    .addFile("file", "video.mp4", videoFile)
                    .url(upLoadVideoUrl)//
                    .params(params)
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            pb_addLoading_progressBar.setVisibility(View.GONE);
                            //恢复编辑状态
                            recoverClick();
                            ToastUtils.showToastInUIThread(UIUtils.getContext(), "上传视频失败");
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            // 成功了上传shipin
                            Map<String, String> map = SendAuthoCodeParse.sendAuthoCodeJson(response);
                            String response_code = map.get("response_code");
                            if (!"0".equals(response_code)) {
                                pb_addLoading_progressBar.setVisibility(View.GONE);
                                //恢复编辑状态
                                recoverClick();
                                ToastUtils.showToastInUIThread(UIUtils.getContext(), "上传视频失败");
                            } else {
                                //解析得到视频网址,开始发送
                                Map<String, String> contentParseMap = ContentParse.contentParse(response);
                                //上传成功在请求
                                //关闭点击
                                cancelClick();
                                //得到日期
                                String needData = getCurrentData();
                                if(needData != null) {
                                    OkHttpUtils.post().url(sendDynamicUrl).addParams("user_id", mUserId)
                                            .addParams("content",et_add_input.getText().toString().trim())
                                            .addParams("video_url",contentParseMap.get("content"))
                                            .addParams("trend_date",needData)
                                            .build().execute(new StringCallback() {
                                        @Override
                                        public void onError(Call call, Exception e, int id) {
                                            pb_addLoading_progressBar.setVisibility(View.GONE);
                                            //恢复编辑状态
                                            recoverClick();
                                            ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                                        }

                                        @Override
                                        public void onResponse(String response, int id) {
                                            try {
                                                pb_addLoading_progressBar.setVisibility(View.GONE);
                                                //恢复编辑状态
                                                recoverClick();
                                                // 成功了，只是返回来请求的json数据,还需要解析
                                                Map<String, String> map = SendAuthoCodeParse.sendAuthoCodeJson(response);
                                                String response_code = map.get("response_code");
                                                //需要判断各种响应码,获取具体错误信息
                                                String responseContent = RequestResponseUtils.getResponseContent(response_code);

                                                if ("0".equals(response_code)) {
                                                    //成功
                                                    ToastUtils.showToastInUIThread(UIUtils.getContext(), "发送成功");
                                                    AddDynamicActivity.this.finish();
                                                    //刷xin动态列表
                                                    MyDynamicActivity.mMyDynamicActivity.refreshData(mUserId);
                                                } else if (responseContent != null) {
                                                    ToastUtils.showToastInUIThread(UIUtils.getContext(), responseContent);
                                                }
                                            } catch (Exception e) {
                                                ToastUtils.showToastInUIThread(UIUtils.getContext(), "请求超时");
                                                //动画关闭
                                                pb_addLoading_progressBar.setVisibility(View.GONE);
                                                //恢复编辑状态
                                                recoverClick();
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });
        }else {
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "视频路径不存在");
            return;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK) {
            if(requestCode==100 ) {
                if(data != null) {
                    boolean videoStatus = data.getBooleanExtra("videoStatus", false);
                    String videoPath = data.getStringExtra("videoPath");
                    //获取视频的缩略图
                    bitmap = BitmapUtils.getVideoThumbnail(videoPath, 200, 200, MediaStore.Video.Thumbnails.MINI_KIND);
                    if(add_img != null) {
                        add_img.setVisibility(View.VISIBLE);
                        add_delete.setVisibility(View.VISIBLE);
                        add_img.setImageBitmap(bitmap);
                    }
                    //根据拍摄视频类返回的状态来判断图标的显示
                    if(videoStatus && ib_add_video !=null) {
                        ib_add_video.setVisibility(View.GONE);
                    }else {
                        ib_add_video.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }
    private void cancelClick() {
        rl_add_back.setClickable(false);
        rl_addDynamic_send.setClickable(false);
        ib_add_video.setClickable(false);
        //让EditText失去焦点，不能输入
        et_add_input.setFocusable(false);
    }

    /**
     * 恢复默认可以点击
     */
    private void recoverClick() {
        //让EditText恢复编辑状态
        RecoverClickUtils.recoverClick(et_add_input);
        //其他控件可以点击
        rl_add_back.setClickable(true);
        rl_addDynamic_send.setClickable(true);
        ib_add_video.setClickable(true);
    }

    /**
     * 得到当前的日期  20160717
     */
    public String getCurrentData() {

        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);

        int month = calendar.get(Calendar.MONTH) + 1;

        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        return year + "" + month + dayOfMonth;
    }

    /**
     * 得到图片的地址file
     */
    public File getVideoFile() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "SD卡不可用");
            return null;
        }

        File fileDir = new File(Environment.getExternalStorageDirectory(), "video");
        File file = new File(fileDir, "video.mp4");
        if (!file.exists()) {
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "视频路径不存在，请重试");
            return null;
        }
        //大于30M不让发送
        if (file.length() > 30 * 1024 * 1024) {
            ToastUtils.showToastInUIThread(UIUtils.getContext(), "上传的视频大小不能超过30M");
            return null;
        }
        return file;
    }
}
