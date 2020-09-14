package com.sky.lamp.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import cn.bingoogolapple.bgabanner.BGABanner;
import com.github.jdsjlzx.ItemDecoration.DividerDecoration;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.google.gson.Gson;
import com.sky.lamp.R;
import com.sky.lamp.adapter.StudyAdapter;
import com.sky.lamp.http.AppService;
import com.sky.lamp.http.MyApi;
import com.sky.lamp.response.BannerResponse;
import com.sky.lamp.response.StudyListResponse;
import com.sky.lamp.utils.ImageLoadUtils;
import com.sky.lamp.utils.MySubscriber;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.sky.lamp.utils.RxSPUtilTool;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import java.util.ArrayList;

/**
 * Created by zhangfy on 2018/8/1.
 */

public class StudyListFrament extends DelayBaseFragment {

    StudyAdapter productListAdapter;
    int page = 1;
    @BindView(R.id.recyleListView)
    LRecyclerView recyleListView;
    @BindView(R.id.titleTv)
    TextView titleTv;
    @BindView(R.id.bannerView)
    BGABanner bannerView;

    @Override
    protected void showDelayData() {
        queryBanner();
        queryList();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_study, null);
        ButterKnife.bind(this, view);
        recyleListView = view.findViewById(R.id.recyleListView);
        productListAdapter = new StudyAdapter(StudyAdapter.ProductViewHolder.class);
        LRecyclerViewAdapter lRecyclerViewAdapter = new LRecyclerViewAdapter(productListAdapter);
        recyleListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        DividerDecoration divider = new DividerDecoration.Builder(getActivity()).setHeight(R.dimen.default_divider_height)
                //                .setPadding(R.dimen.default_divider_padding)
                .setColorResource(R.color.bg_activity2).build();
        recyleListView.addItemDecoration(divider);
        recyleListView.setAdapter(lRecyclerViewAdapter);
        recyleListView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                queryList();
            }
        });
        recyleListView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                ++page;
                queryList();
            }
        });
        recyleListView.setLoadMoreEnabled(true);
        lRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                StudyDetailsActivity.startUI(getActivity(), productListAdapter.getItems().get(position).a_id);
            }
        });
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

        titleTv.setText("学习园地");
        return view;
    }

    public void queryBanner() {
        AppService.createApi(MyApi.class).getImgs(2).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new MySubscriber<BannerResponse>() {
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
                RxSPUtilTool.putJSONCache(getActivity(), "banner2", new Gson().toJson(response));
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

    private void queryList() {
        AppService.createApi(MyApi.class).getArticle(page).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new MySubscriber<StudyListResponse>() {
            @Override
            public void onStart() {
                super.onStart();

            }

            @Override
            public void onError(Throwable error) {
                super.onError(error);
                recyleListView.refreshComplete(10);
            }

            @Override
            public void onCompleted() {
                recyleListView.refreshComplete(10);
            }

            @Override
            public void onNext(final StudyListResponse response) {
                if (page == 1) {
                    productListAdapter.clear();
                }
                productListAdapter.addAll(response.data);
            }
        });
    }
}
