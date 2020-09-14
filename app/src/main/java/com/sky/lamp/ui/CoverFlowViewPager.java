package com.sky.lamp.ui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.event.RefreshEvent;
import com.sky.lamp.R;
import com.sky.lamp.response.ProductListResponse;
import com.sky.lamp.utils.ImageLoadUtils;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * 实现封面浏览
 * Created by alex_mahao on 2016/8/25.
 */
public class CoverFlowViewPager extends RelativeLayout implements OnPageSelectListener {


    /**
     * 适配器
     */
    private CoverFlowAdapter mAdapter;

    /**
     * 用于左右滚动
     */
    public ViewPager mViewPager;

    /**
     * 需要显示的视图集合
     */
    private List<View> mViewList = new ArrayList<>();

    private OnPageSelectListener listener;
    private Context mContext;
    ProductListResponse mResponse;

    public CoverFlowViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        inflate(context, R.layout.widget_cover_flow, this);
        mViewPager = findViewById(R.id.vp_conver_flow);
        init();
    }

    /**
     * 初始化方法
     */
    private void init() {
        // 构造适配器，传入数据源
        mAdapter = new CoverFlowAdapter(mViewList, getContext());
        // 设置选中的回调
        mAdapter.setOnPageSelectListener(this);
        // 设置适配器
        mViewPager.setAdapter(mAdapter);
        // 设置滑动的监听，因为adpter实现了滑动回调的接口，所以这里直接设置adpter
        mViewPager.addOnPageChangeListener(mAdapter);
        // 自己百度
        mViewPager.setOffscreenPageLimit(10);

        // 设置触摸事件的分发
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 传递给ViewPager 进行滑动处理
                return mViewPager.dispatchTouchEvent(event);
            }
        });

    }

    /**
     * 设置显示的数据，进行一层封装
     */
    public void setViewList(ProductListResponse response) {
        if (response == null) {
            return;
        }
        this.mResponse = response;
        mViewList.clear();
        for (ProductListResponse.DataBean dataBean : response.data) {
            View item = LayoutInflater.from(getContext()).inflate(R.layout.item_home,null);
            ImageView img = item.findViewById(R.id.imageView);
            // 设置padding 值，默认缩小
            item.setPadding(CoverFlowAdapter.sWidthPadding, CoverFlowAdapter.sHeightPadding, CoverFlowAdapter.sWidthPadding, CoverFlowAdapter.sHeightPadding);
            mViewList.add(item);
            ImageLoadUtils.loadImage(dataBean.thumb_image,img);
        }
        // 刷新数据
        mAdapter.notifyDataSetChanged();
        select(0);
    }


    /**
     * 当将某一个作为最中央时的回调
     * @param listener
     */
    public void setOnPageSelectListener(OnPageSelectListener listener) {
        this.listener = listener;
    }


    // 显示的回调
    @Override
    public void select(int position) {
        if (listener != null) {
            listener.select(position);
        }
        if (mResponse != null) {
            EventBus.getDefault().post(new RefreshEvent(mResponse.data.get(position).model));
        }
    }

    @Override
    public void click(int position) {
        if (listener != null) {
            listener.click(position);
        }
    }

}
