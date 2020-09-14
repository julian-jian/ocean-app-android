package com.sky.lamp.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.bingoogolapple.bgabanner.BGABanner;
import com.event.RefreshEvent;
import com.google.gson.Gson;
import com.sky.lamp.MainActivity;
import com.sky.lamp.MyApplication;
import com.sky.lamp.R;
import com.sky.lamp.adapter.ProductListAdapter;
import com.sky.lamp.http.AppService;
import com.sky.lamp.http.MyApi;
import com.sky.lamp.response.BannerResponse;
import com.sky.lamp.response.ProductListResponse;
import com.sky.lamp.utils.ImageLoadUtils;
import com.sky.lamp.utils.MySubscriber;
import com.sky.lamp.utils.RxSPUtilTool;
import com.vondear.rxtools.RxActivityTool;

import me.jessyan.autosize.AutoSizeConfig;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import java.util.ArrayList;

/**
 * Created by zhangfy on 2018/8/1.
 */

public class HomeFragment extends DelayBaseFragment {
    @BindView(R.id.bannerView)
    BGABanner bannerView;
    @BindView(R.id.searchEt)
    TextView searchEt;
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.coverFlowVP)
    CoverFlowViewPager coverFlowVP;
    Unbinder unbinder;
    ProductListResponse mProductResponse;
    private ProductListAdapter productListAdapter;

    @Override
    protected void showDelayData() {
        queryBanner();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, null);
        productListAdapter = new ProductListAdapter(ProductListAdapter.ProductViewHolder.class);
        bannerView = view.findViewById(R.id.bannerView);
        unbinder = ButterKnife.bind(this, view);
        searchEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RxActivityTool.skipActivity(getActivity(), ProductListActivity.class);
            }
        });

        Drawable drawable = getResources().getDrawable(R.drawable.search);
        drawable.setBounds(0, 0, dp2px(20), dp2px(20));
        searchEt.setCompoundDrawables(drawable, null, null, null);

        AutoSizeConfig.getInstance().setCustomFragment(true);
        queryList();
        EventBus.getDefault().register(this);
        bannerView.setDelegate(new BGABanner.Delegate<CardView, String>() {
            @Override
            public void onBannerItemClick(BGABanner banner, CardView itemView, String model, int position) {
            }
        });
        bannerView.setAdapter(new BGABanner.Adapter<CardView, String>() {
            @Override
            public void fillBannerItem(BGABanner banner, CardView itemView, String model, int position) {
                ImageLoadUtils.loadImage(model, (ImageView) itemView.findViewById(R.id.imageView));
            }
        });
        coverFlowVP.setOnPageSelectListener(new OnPageSelectListener() {
            @Override
            public void select(int position) {

            }

            @Override
            public void click(int position) {
                ProductDetailsActivity.startUI(getActivity(),mProductResponse.data.get(position).id);
            }
        });
        return view;
    }

    @OnClick({R.id.moreTv,R.id.nav1Iv})
    public void moreClick(){
        MainActivity activity = (MainActivity)getActivity();
        activity.tabSelect(1);
    }

    @OnClick(R.id.nav2Iv)
    public void nav2IvClick(){
        MainActivity activity = (MainActivity)getActivity();
        activity.tabSelect(2);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    private int dp2px(float dpValue) {
        final float scale = MyApplication.getInstance().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    public void queryBanner() {
        AppService.createApi(MyApi.class).getImgs(1).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new MySubscriber<BannerResponse>() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onCompleted() {

            }

            @Override
            public void onNext(final BannerResponse response) {
                updateBannerView(response);
                RxSPUtilTool.putJSONCache(getActivity(), "banner", new Gson().toJson(response));
            }
        });
    }

    private void queryList() {
        AppService.createApi(MyApi.class).getReGoods().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new MySubscriber<ProductListResponse>() {
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
            public void onNext(final ProductListResponse response) {
                mProductResponse = response;
                productListAdapter.clear();
                productListAdapter.addAll(response.data);
                // 初始化数据
                coverFlowVP.setViewList(response);
            }
        });
    }

    private void updateBannerView(BannerResponse response) {
        ArrayList list = new ArrayList();
        for (BannerResponse.DataBean data : response.data) {
            list.add(data.banner_imgfile);
        }
        bannerView.setData(R.layout.item_banner,list , null);
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
        unbinder.unbind();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshContentView(RefreshEvent refreshEvent){
        textView.setText(refreshEvent.event);
    }
}
