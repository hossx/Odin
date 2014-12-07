package com.coinport.odin.adapter;

import android.content.Context;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.coinport.odin.R;
import com.coinport.odin.obj.OrderItem;
import com.coinport.odin.util.Constants;

import java.util.ArrayList;

public class OrderAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context = null;
    private Time timeFormat = new Time();

    private ArrayList<OrderItem> orderItems = null;

    public OrderAdapter setOrderItems(ArrayList<OrderItem> orderItems) {
        if (orderItems == null)
            this.orderItems = null;
        else
            this.orderItems = new ArrayList<>(orderItems);
        return this;
    }

    public OrderAdapter(Context context) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (orderItems == null)
            return 0;
        else
            return orderItems.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.order_item, null);
        }
        OrderItem oi = orderItems.get(position);

        TextView time = (TextView) convertView.findViewById(R.id.order_time);
        TextView operation = (TextView) convertView.findViewById(R.id.order_operation);
        TextView status = (TextView) convertView.findViewById(R.id.order_status);
        Button cancel = (Button) convertView.findViewById(R.id.order_cancel_button);
        TextView sPrice = (TextView) convertView.findViewById(R.id.order_submit_price);
        TextView sQuantity = (TextView) convertView.findViewById(R.id.order_submit_quantity);
        TextView price = (TextView) convertView.findViewById(R.id.order_actual_price);
        TextView quantity = (TextView) convertView.findViewById(R.id.order_actual_quantity);
//        TableLayout tl = (TableLayout) convertView.findViewById(R.id.order_table);

        timeFormat.set(oi.getSubmitTime());
        time.setText(timeFormat.format("%Y-%m-%d %k:%M:%S"));
        if (oi.getOperation().equals("Buy")) {
            operation.setText(context.getString(R.string.trade_buy));
            operation.setTextColor(Constants.CP_GREEN);
//            tl.setBackgroundResource(R.drawable.green_input_border);
        } else {
            operation.setText(context.getString(R.string.trade_sell));
            operation.setTextColor(Constants.CP_RED);
//            tl.setBackgroundResource(R.drawable.red_input_border);
        }

        status.setText(context.getString(Constants.ORDER_STATUS_MAP.get(oi.getStatus())));
        if (oi.getStatus() == 0 || oi.getStatus() == 1) {
            cancel.setVisibility(View.VISIBLE);
        } else {
            cancel.setVisibility(View.GONE);
        }
        sPrice.setText(oi.getSubmitPrice());
        sQuantity.setText(oi.getSubmitAmount());
        price.setText(oi.getActualPrice());
        quantity.setText(oi.getActualAmount());
        return convertView;
    }
}
