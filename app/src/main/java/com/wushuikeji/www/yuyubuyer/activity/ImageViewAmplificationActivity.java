package com.wushuikeji.www.yuyubuyer.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.wushuikeji.www.yuyubuyer.R;
import com.wushuikeji.www.yuyubuyer.view.SmoothImageView;

public class ImageViewAmplificationActivity extends AppCompatActivity {

    private SmoothImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_details);
        int[] imagesId = getIntent().getIntArrayExtra("imagesId");
        int mPosition = getIntent().getIntExtra("position", 0);
        int mLocationX = getIntent().getIntExtra("locationX", 0);
        int mLocationY = getIntent().getIntExtra("locationY", 0);
        int mWidth = getIntent().getIntExtra("width", 0);
        int mHeight = getIntent().getIntExtra("height", 0);

        mImageView = new SmoothImageView(this);
        mImageView.setOriginalInfo(mWidth, mHeight, mLocationX, mLocationY);
        mImageView.transformIn();
        mImageView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        mImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        setContentView(mImageView);
        mImageView.setImageResource(imagesId[mPosition]);

    }
    @Override
    public void onBackPressed() {
        mImageView.setOnTransformListener(new SmoothImageView.TransformListener() {
            @Override
            public void onTransformComplete(int mode) {
                if (mode == 2) {
                    finish();
                }
            }
        });
        mImageView.transformOut();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            overridePendingTransition(0, 0);
        }
    }
}
