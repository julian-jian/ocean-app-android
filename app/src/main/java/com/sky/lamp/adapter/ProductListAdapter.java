package com.sky.lamp.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.sky.lamp.R;
import com.sky.lamp.response.ProductListResponse;
import com.sky.lamp.utils.ImageLoadUtils;

/**
 * Created by sky on 2018/1/3.
 */

public class ProductListAdapter extends ModelRecyclerAdapter<ProductListResponse.DataBean> {

    public ProductListAdapter(Class<? extends ModelViewHolder> viewHolderClass) {
        super(viewHolderClass);
    }

    @RecyclerItemViewId(R.layout.item_product_list)
    public static class ProductViewHolder extends ModelViewHolder<ProductListResponse.DataBean> {
        @BindView(R.id.imgIv)
        ImageView imgIv;
        @BindView(R.id.nameTv)
        TextView nameTv;

        public ProductViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void convert(ProductListResponse.DataBean item, ModelRecyclerAdapter adapter, Context context, int positon) {
            //            ImageLoadUtils.loadImage(item.);
//            if (positon % 2 == 0) {
//                imgIv.setPadding(0,dp2px(4),dp2px(2),0);
//            }else{//右边
//                imgIv.setPadding(dp2px(2),dp2px(4),0,0);
//            }
            nameTv.setText(item.model);
            ImageLoadUtils.loadImage(item.thumb_image, imgIv);
        }
    }
}
