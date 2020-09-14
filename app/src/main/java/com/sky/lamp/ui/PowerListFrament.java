package com.sky.lamp.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.orhanobut.logger.Logger;
import com.sky.lamp.R;
import com.sky.lamp.adapter.PowerListAdapter;
import com.sky.lamp.http.AppService;
import com.sky.lamp.http.MyApi;
import com.sky.lamp.response.PowerListResponse;
import com.sky.lamp.utils.MySubscriber;
import com.vondear.rxtools.RxActivityTool;
import com.vondear.rxtools.RxKeyboardTool;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.vondear.rxtools.RxImageTool.dp2px;

/**
 * Created by zhangfy on 2018/8/1.
 */

public class PowerListFrament extends DelayBaseFragment {

    PowerListAdapter powerListAdapter;
    int page = 1;
    @BindView(R.id.searchEt)
    EditText seachEt;
    @BindView(R.id.recyleListView)
    LRecyclerView recyleListView;
    Unbinder unbinder;
    @Override
    protected void showDelayData() {
        queryList();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chang_power, null);
        unbinder = ButterKnife.bind(this, view);
        powerListAdapter = new PowerListAdapter(PowerListAdapter.ProductViewHolder.class);
        LRecyclerViewAdapter lRecyclerViewAdapter = new LRecyclerViewAdapter(powerListAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this.getContext(), 2);
        recyleListView.addItemDecoration(new SpacesItemDecoration(dp2px(7.5f)));
        recyleListView.setLayoutManager(gridLayoutManager);
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
                //                ProductListActivity
                Bundle bundle = new Bundle();
                bundle.putString("c_id", powerListAdapter.getItems().get(position).cate_id);
                bundle.putString("name", powerListAdapter.getItems().get(position).cate_name);
                RxActivityTool.skipActivity(getActivity(), ProductListActivity.class, bundle);
            }
        });
        seachEt.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    Logger.i("onkey KEYCODE_ENTER");
                    // 过滤换行符
                    seachEt.setText(seachEt.getText().toString().trim());
                    search();
                    return true;
                }
                if (keyCode == KeyEvent.KEYCODE_SEARCH && event.getAction() == KeyEvent.ACTION_UP) {
                    Logger.i("onkey KEYCODE_SEARCH");
                    seachEt.setText(seachEt.getText().toString().trim());
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
        return view;
    }

    private void search() {
        page = 1;
        recyleListView.refresh();
        RxKeyboardTool.hideSoftInput(getActivity(), seachEt);
    }

    private void queryList() {
        AppService.createApi(MyApi.class).powerList(page, seachEt.getText().toString()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new MySubscriber<PowerListResponse>() {
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
            public void onNext(final PowerListResponse response) {
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
    public void onClick() {
        search();
    }
}
