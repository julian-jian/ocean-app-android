package com.sky.lamp.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.sky.lamp.BaseActivity;
import com.sky.lamp.MyApplication;
import com.sky.lamp.R;
import com.sky.lamp.http.AppService;
import com.sky.lamp.http.MyApi;
import com.sky.lamp.response.ProductDetailsResponse;
import com.sky.lamp.utils.ImageLoadUtils;
import com.sky.lamp.utils.MySubscriber;
import com.sky.lamp.view.TitleBar;
import com.vondear.rxtools.RxActivityTool;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ProductDetailsActivity extends BaseActivity {
    @BindView(R.id.actionBar)
    TitleBar actionBar;
    @BindView(R.id.imgIv)
    ImageView imgTv;
    @BindView(R.id.contentLl)
    LinearLayout contentLl;

    public static void startUI(Context context, String id) {
        Intent intent = new Intent(context, ProductDetailsActivity.class);
        intent.putExtra("id", id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);
        actionBar.initLeftImageView(this);
        actionBar.getTitleTextView().setText("信息详情");
        query();
    }

    private void query() {
        AppService.createApi(MyApi.class).goodsDetail(getIntent().getStringExtra("id")).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new MySubscriber<ProductDetailsResponse>() {
            @Override
            public void onStart() {
                super.onStart();

            }

            @Override
            public void onError(Throwable error) {
                super.onError(error);
            }

            @Override
            public void onCompleted() {
            }

            @Override
            public void onNext(final ProductDetailsResponse response) {
                ProductDetailsResponse.DataBean data = response.data;
                if (response.code == 200) {
                    addItem("使用变电站:" + data.cate_name);
                    addItem("厂家:" + data.manufactor);
                    addItem("型号:" + data.model);
                    addItem("操作电压:"+data.operat_voltage);
                    addItem("合闸时间:"+data.close_time+"/ ms");
                    addItem("合闸不同期要求:"+data.close_distinct);
                    addItem("分闸时间:" + data.branch_time+"/ ms");
                    addItem("分闸不同期:" + data.branch_distinct+"/ ms");
                    addItem("合-分闸时间:" + data.cb_time+"/ ms");
                    addItem("主回路电阻:" + data.loop_resistance+"/ μΩ");
                    addItem("速度定义:" + data.speed_definition);
                    addItem("标准行程:" + data.standard_stroke);
                    addItem("合闸速度:" + data.close_speed);
                    addItem("分闸速度:" + data.branch_speed);
                    addItem("传感器类型:" + data.sensor_type);
                    addItem("使用仪器:" + data.use_instrument);
                    addItem("合闸端子:" + data.close_terminal);
                    addItem("分闸端子:" + data.branch_terminal);
                    addItem("分闸端子:" + data.branch_terminal);
                    addItem("端子位置:" + data.terminal_position);
                    addItem("备注:" + data.remarks);
                    imgTv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle bundler = new Bundle();
                            bundler.putString("url", response.data.circuit_image);
                            RxActivityTool.skipActivity(ProductDetailsActivity.this,ImageDetailsActivity.class,bundler);
                        }
                    });
                    ImageLoadUtils.loadImage(response.data.circuit_image, imgTv);

//                    webView.loadDataWithBaseURL("about:blank", response.data.content, "text/html", "utf-8", null);
                }
            }
        });
    }

    public void addItem(String str){
        TextView textView = new TextView(this);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,17);
        textView.setText(str);
        textView.setPadding(0,0,0,dp2px(4));
        contentLl.addView(textView);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    private int dp2px(float dpValue) {
        final float scale = MyApplication.getInstance().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
