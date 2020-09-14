package com.sky.lamp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * hodler抽象类，支持任何数据类型
 *
 * @param <T>
 */
public abstract class ModelViewHolder<T> extends RecyclerView.ViewHolder {

    public ModelViewHolder(View itemView) {
        super(itemView);
    }

    /**
     * 这个是我们真正在实际使用的类中的绑定数据的方法
     *
     * @param item    bean类型
     * @param adapter adpter对象
     * @param context context对象
     * @param positon 位置
     */
    public abstract void convert(T item, ModelRecyclerAdapter adapter, Context context, int positon);
}