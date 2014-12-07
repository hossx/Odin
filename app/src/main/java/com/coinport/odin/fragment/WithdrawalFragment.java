package com.coinport.odin.fragment;

import android.os.Bundle;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.coinport.odin.R;
import com.coinport.odin.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;

public class WithdrawalFragment extends DWFragmentCommon {
    private String currency;
    private View view;
    private LinearLayout bankSelector;
    private LinearLayout address;
    private LinearLayout memo;
    private TextView memoLabel;
    private TextView nxtPubkeyDesc;

    private Time timeFormat = new Time();

    private static ArrayList<String> items;

    static {
        items = new ArrayList<>();
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

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, items);
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
        switch (currency) {
            case "CNY":
                setItemsVisibility(EnumSet.of(OptItem.BANK));

                break;
            case "BTSX":
                setItemsVisibility(EnumSet.of(OptItem.ADDRESS, OptItem.MEMO));
                memoLabel.setText(getString(R.string.withdrawal_memo));

                break;
            case "NXT":
                setItemsVisibility(EnumSet.of(OptItem.ADDRESS, OptItem.MEMO, OptItem.PUBKEY_DESCRIPTION));
                memoLabel.setText(getString(R.string.withdrawal_nxt_pubkey));

                break;
            default:
                setItemsVisibility(EnumSet.of(OptItem.ADDRESS));

                break;
        }
        updateWithdrawalHistory();
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

    private void updateWithdrawalHistory() {
        ListView lv = (ListView) view.findViewById(R.id.withdrawal_history);
        lv.setFocusable(false);

        ArrayList<HashMap<String, String>> dhList = new ArrayList<>();
        JSONArray jsonArray = Util.getJsonArrayFromFile(getActivity(), "deposit_history_mock.json");
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); ++i) {
                HashMap<String, String> fields = new HashMap<>();
                try {
                    JSONObject jsonObj = jsonArray.getJSONObject(i);
                    timeFormat.set(jsonObj.getLong("updated"));
                    fields.put("transfer_time", timeFormat.format("%Y-%m-%d %k:%M:%S"));
                    fields.put("transfer_amount", jsonObj.getJSONObject("amount").getString("display"));
                    fields.put("transfer_status", getString(Util.transferStatus.get(jsonObj.getInt("status"))));
                    dhList.add(fields);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        SimpleAdapter adapter = new SimpleAdapter(getActivity(), dhList, R.layout.transfer_item, new String[]{
            "transfer_time", "transfer_amount", "transfer_status"}, new int[] {R.id.transfer_time, R.id.transfer_amount,
            R.id.transfer_status});
        lv.setAdapter(adapter);
    }
    private enum OptItem {
        BANK, ADDRESS, MEMO, PUBKEY_DESCRIPTION
    }
}
