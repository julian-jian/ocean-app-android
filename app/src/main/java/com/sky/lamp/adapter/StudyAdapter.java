package com.sky.lamp.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.sky.lamp.R;
import com.sky.lamp.response.StudyListResponse;
import com.sky.lamp.utils.ImageLoadUtils;

/**
 * Created by sky on 2018/1/3.
 */

public class StudyAdapter extends ModelRecyclerAdapter<StudyListResponse.DataBean> {

    public StudyAdapter(Class<? extends ModelViewHolder> viewHolderClass) {
        super(viewHolderClass);
    }

    @RecyclerItemViewId(R.layout.item_study)
    public static class ProductViewHolder extends ModelViewHolder<StudyListResponse.DataBean> {
        @BindView(R.id.titleTv)
        TextView nameTv;
        @BindView(R.id.dateTv)
        TextView dateTv;
        @BindView(R.id.imageView)
        ImageView imageView;

        public ProductViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void convert(StudyListResponse.DataBean item, ModelRecyclerAdapter adapter, Context context, int positon) {
            //            ImageLoadUtils.loadImage(item.);
            nameTv.setText(item.a_title);
            ImageLoadUtils.loadImage(item.thumb_image, imageView);
            dateTv.setText(item.date);
        }
    }
}
