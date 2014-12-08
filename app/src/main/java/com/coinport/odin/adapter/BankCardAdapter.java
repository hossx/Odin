package com.coinport.odin.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.coinport.odin.R;
import com.coinport.odin.obj.ViewHolder;

import java.util.ArrayList;

public class BankCardAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<String> list;
	public BankCardAdapter(Context context, ArrayList<String> list){
		this.context = context;
		this.list = list;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
        ViewHolder viewHolder = null;
        if (arg1 == null && list.size() != 0) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            arg1 = inflater.inflate(R.layout.bank_card_item, null);
            viewHolder.textView = (TextView)arg1.findViewById(R.id.bank_card_text);
            viewHolder.button = (android.widget.Button) arg1.findViewById(R.id.delete_bank_card);
            arg1.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) arg1.getTag();
        }
        viewHolder.textView.setText(list.get(arg0));
        final String str = viewHolder.textView.getText().toString();
        viewHolder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("bank card selector", str);
            }
        });
        return arg1;
	}

}
