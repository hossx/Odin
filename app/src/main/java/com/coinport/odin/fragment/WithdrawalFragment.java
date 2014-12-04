package com.coinport.odin.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.coinport.odin.R;
import com.coinport.odin.library.ptr.PullToRefreshScrollView;
import com.coinport.odin.util.Util;

import java.util.ArrayList;

/**
 * Created by hoss on 14-12-3.
 */
public class WithdrawalFragment extends Fragment {
    private String currency;
    private View view;

    private static ArrayList<String> items;
    private ArrayAdapter<String> adapter;

    static {
        items = new ArrayList<String>();
        items.add("A");
        items.add("B");
        items.add("C");
        items.add("D");
        items.add("E");
        items.add("F");
        items.add("G");
        items.add("H");
    }
    public WithdrawalFragment(String currency) {
        this.currency = currency;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.withdrawal_fragment, container, false);
        updateWithdrawalInfo();
        return view;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
        if (view != null)
            updateWithdrawalInfo();
    }

    private void updateWithdrawalInfo() {
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, items);
        ListView lv = (ListView) view.findViewById(R.id.withdrawal_history);
        lv.setFocusable(false);
        lv.setAdapter(adapter);
    }
}
