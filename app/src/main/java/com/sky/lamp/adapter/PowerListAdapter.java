package com.sky.lamp.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.sky.lamp.R;
import com.sky.lamp.response.PowerListResponse;
import com.sky.lamp.utils.ImageLoadUtils;

/**
 * Created by sky on 2018/1/3.
 */

public class PowerListAdapter extends ModelRecyclerAdapter<PowerListResponse.DataBean> {

    public PowerListAdapter(Class<? extends ModelViewHolder> viewHolderClass) {
        super(viewHolderClass);
    }

    @RecyclerItemViewId(R.layout.item_power_list)
    public static class ProductViewHolder extends ModelViewHolder<PowerListResponse.DataBean> {
        @BindView(R.id.imgIv)
        ImageView imgIv;
        @BindView(R.id.nameTv)
        TextView nameTv;
        public boolean isHome = true;
        public ProductViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void convert(PowerListResponse.DataBean item, ModelRecyclerAdapter adapter, Context context, int positon) {
            //            ImageLoadUtils.loadImage(item.);
            nameTv.setText(item.cate_name);
            ImageLoadUtils.loadImage(item.cate_img, imgIv);
        }
    }
}
