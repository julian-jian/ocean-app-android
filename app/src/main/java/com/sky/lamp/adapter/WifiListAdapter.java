package com.sky.lamp.adapter;

import com.guo.duoduo.wifidetective.entity.RouterInfo;
import com.sky.lamp.R;
import com.sky.lamp.response.WifiResponse;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sky on 2018/1/3.
 */

public class WifiListAdapter extends ModelRecyclerAdapter<RouterInfo> {

    public WifiListAdapter(Class<? extends ModelViewHolder> viewHolderClass) {
        super(viewHolderClass);
    }

    @RecyclerItemViewId(R.layout.item_wifi_list)
    public static class ProductViewHolder extends ModelViewHolder<RouterInfo> {
        @BindView(R.id.mac)
        TextView mac;
        @BindView(R.id.name)
        TextView nameTv;

        public ProductViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void convert(RouterInfo item, ModelRecyclerAdapter adapter, Context context,
                            int positon) {
            mac.setText("mac:" + item.mMac);
            nameTv.setText("ssid:" + item.mSsid);
        }
    }
}
