package com.sky.lamp.ui;

import android.os.Bundle;

import com.bm.library.PhotoView;
import com.sky.lamp.BaseActivity;
import com.sky.lamp.R;
import com.sky.lamp.utils.ImageLoadUtils;
import com.sky.lamp.view.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImageDetailsActivity extends BaseActivity {

    @BindView(R.id.actionBar)
    TitleBar actionBar;
    @BindView(R.id.img)
    PhotoView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_image_details);
        ButterKnife.bind(this);
        actionBar.initLeftImageView(this);
        actionBar.setTitle("图片详情");
        // 启用图片缩放功能
        img.enable();
        img.setMaxScale(50);
        ImageLoadUtils.loadImage(getIntent().getStringExtra("url"), img);
    }

}
