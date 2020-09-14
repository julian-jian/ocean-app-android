package com.sky.lamp.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.github.jdsjlzx.ItemDecoration.DividerDecoration;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.orhanobut.logger.Logger;
import com.sky.lamp.BaseActivity;
import com.sky.lamp.R;
import com.sky.lamp.adapter.ProductListAdapter;
import com.sky.lamp.http.AppService;
import com.sky.lamp.http.MyApi;
import com.sky.lamp.response.ProductListResponse;
import com.sky.lamp.utils.MySubscriber;
import com.sky.lamp.view.TitleBar;
import com.vondear.rxtools.RxKeyboardTool;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.vondear.rxtools.RxImageTool.dp2px;

public class ProductListActivity extends BaseActivity {
    int page = 1;
    @BindView(R.id.searchEt)
    EditText seachEt;
    @BindView(R.id.recyleListView)
    LRecyclerView recyleListView;
    Unbinder unbinder;
    ProductListAdapter powerListAdapter;
    @BindView(R.id.searchLL)
    LinearLayout searchLL;
    @BindView(R.id.actionBar)
    TitleBar titleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        ButterKnife.bind(this);
        if (TextUtils.isEmpty(getIntent().getStringExtra("name"))) {
            titleBar.getTitleTextView().setText("型号列表");
        }else{
            titleBar.getTitleTextView().setText(getIntent().getStringExtra("name")+"-"+"型号列表");
        }
        titleBar.initLeftImageView(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        powerListAdapter = new ProductListAdapter(ProductListAdapter.ProductViewHolder.class);
        final LRecyclerViewAdapter lRecyclerViewAdapter = new LRecyclerViewAdapter(powerListAdapter);
        recyleListView.setLayoutManager(gridLayoutManager);
        DividerDecoration divider = new DividerDecoration.Builder(this).setHeight(R.dimen.default_divider_height)
                //                .setPadding(R.dimen.default_divider_padding)
                .setColorResource(R.color.bg_activity2).build();
        //        GridLayoutManager gridLayoutManager = new GridLayoutManager(this.getContext(), 2);
        //        recyleListView.setLayoutManager(gridLayoutManager);
        recyleListView.addItemDecoration(divider);
        recyleListView.setAdapter(lRecyclerViewAdapter);
        recyleListView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                if (!TextUtils.isEmpty(getIntent().getStringExtra("c_id"))) {//是否是子目录
                    queryCateList();
                }else{
                    queryList();
                }
            }
        });
        recyleListView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                ++page;
                if (!TextUtils.isEmpty(getIntent().getStringExtra("c_id"))) {//是否是子目录
                    queryCateList();
                }else{
                    queryList();
                }
            }
        });
        recyleListView.setLoadMoreEnabled(true);
        lRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ProductDetailsActivity.startUI(ProductListActivity.this, powerListAdapter.getItems().get(position).id);
            }
        });

        seachEt.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    Logger.i("onkey KEYCODE_ENTER");
                    search();
                    return true;
                }
                if (keyCode == KeyEvent.KEYCODE_SEARCH && event.getAction() == KeyEvent.ACTION_UP) {
                    Logger.i("onkey KEYCODE_SEARCH");
                    search();
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_DEL) {
                    return false;
                }
                return false;
            }
        });
        Drawable drawable = getResources().getDrawable(R.drawable.search);
        drawable.setBounds(0, 0, dp2px(20), dp2px(20));
        seachEt.setCompoundDrawables(drawable, null, null, null);
        if (!TextUtils.isEmpty(getIntent().getStringExtra("c_id"))) {//是否是子目录
            searchLL.setVisibility(View.GONE);
        }

        recyleListView.refresh();
    }

    private void search() {
        page = 1;
        recyleListView.refresh();
        RxKeyboardTool.hideSoftInput(ProductListActivity.this, seachEt);
    }

    void queryCateList() {
        AppService.createApi(MyApi.class).getGoods(page, getIntent().getStringExtra("c_id")).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new MySubscriber<ProductListResponse>() {
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
            public void onNext(final ProductListResponse response) {
                if (response.code == 200) {
                    if (page == 1) {
                        powerListAdapter.clear();
                    }
                    powerListAdapter.addAll(response.data);
                } else {
                }
            }
        });
    }

    void queryList() {
        AppService.createApi(MyApi.class).searchGoods(page, seachEt.getText().toString().trim()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new MySubscriber<ProductListResponse>() {
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
            public void onNext(final ProductListResponse response) {
                if (response.code == 200) {
                    if (page == 1) {
                        powerListAdapter.clear();
                    }
                    powerListAdapter.addAll(response.data);
                } else {
                }
            }
        });
    }
}
