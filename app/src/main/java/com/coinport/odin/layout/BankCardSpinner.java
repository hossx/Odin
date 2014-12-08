package com.coinport.odin.layout;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;

import com.coinport.odin.R;
import com.coinport.odin.adapter.BankCardAdapter;
import com.coinport.odin.dialog.BankCardSelectDialog;

import java.util.ArrayList;

/**
 * Created by hoss on 14-12-8.
 */
public class BankCardSpinner extends Spinner implements AdapterView.OnItemClickListener {
    public static BankCardSelectDialog dialog = null;
    private ArrayList<String> list;
    public static String text;

    public BankCardSpinner(Context context) {
        super(context);
    }

    public BankCardSpinner(Context context, int mode) {
        super(context, mode);
    }

    public BankCardSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BankCardSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BankCardSpinner(Context context, AttributeSet attrs, int defStyleAttr, int mode) {
        super(context, attrs, defStyleAttr, mode);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BankCardSpinner(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, int mode) {
        super(context, attrs, defStyleAttr, defStyleRes, mode);
    }

    @Override
    public boolean performClick() {
        Context context = getContext();
        final LayoutInflater inflater = LayoutInflater.from(getContext());
        final View view = inflater.inflate(R.layout.bank_card_spinner, null);
        final ListView listview = (ListView) view.findViewById(R.id.bank_card_spinner_list);
        BankCardAdapter adapters = new BankCardAdapter(context, getList());
        listview.setAdapter(adapters);
        listview.setOnItemClickListener(this);
        dialog = new BankCardSelectDialog(context, R.style.dialog);//创建Dialog并设置样式主题
        LayoutParams params = new LayoutParams(650, LayoutParams.FILL_PARENT);
        dialog.setCanceledOnTouchOutside(true);// 设置点击Dialog外部任意区域关闭Dialog
        dialog.show();
        dialog.addContentView(view, params);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        setSelection(position);
        setText(list.get(position));
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }
    public ArrayList<String> getList() {
        return list;
    }

    public void setList(ArrayList<String> list) {
        this.list = list;
    }

    public void setText(String text) {
        this.text = text;
    }
}
