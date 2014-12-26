package com.coinport.odin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.coinport.odin.R;
import com.coinport.odin.obj.DepthItem;
import com.coinport.odin.util.Constants;
import com.coinport.odin.util.Util;

import java.util.ArrayList;

public class DepthAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context = null;
    private ArrayList<DepthItem> depthItems = null;

    public DepthAdapter setDepthItems(ArrayList<DepthItem> depthItems) {
        if (depthItems == null)
            this.depthItems = null;
        else
            this.depthItems = new ArrayList<>(depthItems);
        return this;
    }

    public DepthAdapter(Context context) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (depthItems == null)
            return 0;
        else
            return depthItems.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.depth_item, null);
//            int width = convertView.getWidth();
        }
        final DepthItem di = depthItems.get(position);
        TextView index = (TextView) convertView.findViewById(R.id.depth_index);
        TextView price = (TextView) convertView.findViewById(R.id.depth_price);
        TextView amount = (TextView) convertView.findViewById(R.id.depth_amount);
//        index.setWidth(width / 3);
//        price.setWidth(width / 3);
        price.setText(Util.autoDisplayDouble(di.getPrice()));
        amount.setText(Util.autoDisplayDouble(di.getAmount()));

        if (di.isBuy()) {
            index.setText(context.getString(R.string.depth_index_buy) + (position + 1));
            index.setTextColor(Constants.CP_GREEN);
            price.setTextColor(Constants.CP_GREEN);
            amount.setTextColor(Constants.CP_GREEN);
        } else {
            index.setText(context.getString(R.string.depth_index_sell) + (depthItems.size() - position));
            index.setTextColor(Constants.CP_RED);
            price.setTextColor(Constants.CP_RED);
            amount.setTextColor(Constants.CP_RED);
        }
        return convertView;
    }
}
