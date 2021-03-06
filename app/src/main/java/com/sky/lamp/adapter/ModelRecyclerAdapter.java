package com.sky.lamp.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView 通用适配器第一版
 * Created by cd5160866 on 16/5/10.
 */
public class ModelRecyclerAdapter<T> extends RecyclerView.Adapter<ModelViewHolder> {
    protected Context mContext;
    /**
     * 通过注释的方式加入的布局item的layoutId
     */
    protected int mLayoutId;
    /**
     * viewholder的实现类类名
     */
    private Class<? extends ModelViewHolder> viewHolderClass;
    /**
     * 数据 即我们的任何类型的bean
     */
    protected List<T> mDatas = new ArrayList<>();

    public ModelRecyclerAdapter(Class<? extends ModelViewHolder> viewHolderClass) {
        this.viewHolderClass = viewHolderClass;
        this.mLayoutId = viewHolderClass.getAnnotation(RecyclerItemViewId.class)
                .value();
    }

    public ModelRecyclerAdapter(Class<? extends ModelViewHolder> viewHolderClass, List<T> Datas) {
        this.viewHolderClass = viewHolderClass;
        this.mLayoutId = viewHolderClass.getAnnotation(RecyclerItemViewId.class)//获取我们的layoutid，我们的类注释后面的部分
                .value();
        mDatas = Datas;
    }

    @Override
    public ModelViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        System.out.println("parent = [" + parent + "], viewType = [" + viewType + "]");
        ModelViewHolder viewHolder = null;
        if (mContext == null)
            mContext = parent.getContext();

        try {
            View converView = LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false);
            viewHolder = viewHolderClass.getConstructor(View.class).newInstance(converView);
            ButterKnife.bind(viewHolder, converView);//将viewhodler于我们的view绑定起来
        } catch (Exception e) {
            e.printStackTrace();
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ModelViewHolder holder, int position) {
        holder.convert(mDatas.get(position), this, mContext, position);//这里更新数据
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public void add(int positon, T data) {
        mDatas.add(positon, data);
        notifyItemInserted(positon);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        }, 500);

    }

    public void add(T data) {
        mDatas.add(data);
        notifyDataSetChanged();

    }

    public void add(List<T> data) {
        mDatas.clear();
        mDatas.addAll(data);
        notifyDataSetChanged();
    }

    public void remove(int position) {
        mDatas.remove(position);
        notifyItemRemoved(position);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        }, 500);
    }

    public void replace(int position, T data) {
        mDatas.remove(position);
        mDatas.add(position == 0 ? position : position - 1, data);
        notifyDataSetChanged();
    }

    public void addAll(List<T> datas) {
        mDatas.addAll(datas);
        notifyDataSetChanged();
    }

    public void clear() {
        mDatas.clear();
        notifyDataSetChanged();
    }

    public List<T> getItems() {
        return mDatas;
    }


}
