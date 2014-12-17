package com.coinport.odin.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.coinport.odin.App;
import com.coinport.odin.R;
import com.coinport.odin.activity.LoginActivity;
import com.coinport.odin.network.NetworkAsyncTask;
import com.coinport.odin.network.NetworkRequest;
import com.coinport.odin.network.OnApiResponseListener;
import com.coinport.odin.obj.OrderItem;
import com.coinport.odin.util.Constants;
import com.coinport.odin.util.Util;

import org.json.JSONArray;

import java.util.ArrayList;

public class OrderAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context = null;
    private Time timeFormat = new Time();

    private ArrayList<OrderItem> orderItems = null;
    private String inCurrency, outCurrency;
    private OnOrderCancelled cancelledHandler = null;

    public OrderAdapter setCancelledHandler(OnOrderCancelled handler) {
        this.cancelledHandler = handler;
        return this;
    }

    public OrderAdapter setOrderItems(ArrayList<OrderItem> orderItems) {
        if (orderItems == null)
            this.orderItems = null;
        else
            this.orderItems = new ArrayList<>(orderItems);
        return this;
    }

    public OrderAdapter(Context context, String inCurrency, String outCurrency) {
        this.context = context;
        this.inCurrency = inCurrency;
        this.outCurrency = outCurrency;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.order_item, null);
        }
        final OrderItem oi = orderItems.get(position);

        TextView time = (TextView) convertView.findViewById(R.id.order_time);
        TextView operation = (TextView) convertView.findViewById(R.id.order_operation);
        TextView status = (TextView) convertView.findViewById(R.id.order_status);
        final Button cancel = (Button) convertView.findViewById(R.id.order_cancel_button);
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
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = String.format(Constants.CANCEL_ORDER_URL, inCurrency, outCurrency, oi.getId());
                    NetworkAsyncTask task = new NetworkAsyncTask(url, Constants.HttpMethod.GET)
                            .setOnSucceedListener(new OnApiResponseListener())
                            .setOnFailedListener(new OnApiResponseListener())
                            .setRenderListener(new NetworkAsyncTask.OnPostRenderListener() {
                                @Override
                                public void onRender(NetworkRequest s) {
                                    if (s.getApiStatus() != NetworkRequest.ApiStatus.SUCCEED) {
                                        if (s.getApiStatus() == NetworkRequest.ApiStatus.UNAUTH) {
                                            Intent intent = new Intent(context, LoginActivity.class);
                                            context.startActivity(intent);
                                        } else {
                                            Toast.makeText(context, context.getString(R.string.cancel_failed),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        orderItems.remove(position);
                                        OrderAdapter.this.notifyDataSetChanged();
                                        if (cancelledHandler != null)
                                            cancelledHandler.onCancelled();
                                    }
                                }
                            });
                    task.execute();
                }
            });
        } else {
            cancel.setVisibility(View.GONE);
        }
        sPrice.setText(oi.getSubmitPrice());
        sQuantity.setText(oi.getSubmitAmount());
        price.setText(oi.getActualPrice());
        quantity.setText(oi.getActualAmount());
        return convertView;
    }

    public interface OnOrderCancelled {
        public void onCancelled();
    }
}
