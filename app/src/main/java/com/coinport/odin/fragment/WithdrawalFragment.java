package com.coinport.odin.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.coinport.odin.R;

import java.util.ArrayList;
import java.util.EnumSet;

/**
 * Created by hoss on 14-12-3.
 */
public class WithdrawalFragment extends DWFragmentCommon {
    private String currency;
    private View view;
    private LinearLayout bankSelector;
    private LinearLayout address;
    private LinearLayout memo;
    private TextView memoLabel;
    private TextView nxtPubkeyDesc;

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
        bankSelector = (LinearLayout) view.findViewById(R.id.withdrawal_bank_card_selector);
        address = (LinearLayout) view.findViewById(R.id.withdrawal_address);
        memo = (LinearLayout) view.findViewById(R.id.withdrawal_memo);
        memoLabel = (TextView) view.findViewById(R.id.withdrawal_memo_label);
        nxtPubkeyDesc = (TextView) view.findViewById(R.id.withdrawal_nxt_pubkey_description);

        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, items);
        ListView lv = (ListView) view.findViewById(R.id.withdrawal_history);
        lv.setFocusable(false);
        lv.setAdapter(adapter);
        updateWithdrawalInfo();
        return view;
    }

    @Override
    public void setCurrency(String currency) {
        this.currency = currency;
        if (view != null)
            updateWithdrawalInfo();
    }

    private void updateWithdrawalInfo() {
        if (currency.equals("CNY")) {
            setItemsVisibility(EnumSet.of(OptItem.BANK));

        } else if (currency.equals("BTSX")) {
            setItemsVisibility(EnumSet.of(OptItem.ADDRESS, OptItem.MEMO));
            memoLabel.setText(getString(R.string.withdrawal_memo));

        } else if (currency.equals("NXT")) {
            setItemsVisibility(EnumSet.of(OptItem.ADDRESS, OptItem.MEMO, OptItem.PUBKEY_DESCRIPTION));
            memoLabel.setText(getString(R.string.withdrawal_nxt_pubkey));

        } else {
            setItemsVisibility(EnumSet.of(OptItem.ADDRESS));

        }
    }

    private void setItemsVisibility(EnumSet<OptItem> opts) {
        bankSelector.setVisibility(View.GONE);
        address.setVisibility(View.GONE);
        memo.setVisibility(View.GONE);
        nxtPubkeyDesc.setVisibility(View.GONE);
        if (opts.contains(OptItem.BANK))
            bankSelector.setVisibility(View.VISIBLE);
        if (opts.contains(OptItem.ADDRESS))
            address.setVisibility(View.VISIBLE);
        if (opts.contains(OptItem.MEMO))
            memo.setVisibility(View.VISIBLE);
        if (opts.contains(OptItem.PUBKEY_DESCRIPTION))
            nxtPubkeyDesc.setVisibility(View.VISIBLE);
    }

    private enum OptItem {
        BANK, ADDRESS, MEMO, PUBKEY_DESCRIPTION
    }
}
