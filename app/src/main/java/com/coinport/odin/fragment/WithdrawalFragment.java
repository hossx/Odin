package com.coinport.odin.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.coinport.odin.App;
import com.coinport.odin.R;
import com.coinport.odin.activity.LoginActivity;
import com.coinport.odin.layout.BankCardSpinner;
import com.coinport.odin.library.ptr.PullToRefreshScrollView;
import com.coinport.odin.network.NetworkAsyncTask;
import com.coinport.odin.network.NetworkRequest;
import com.coinport.odin.network.OnApiResponseListener;
import com.coinport.odin.util.Constants;
import com.coinport.odin.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class WithdrawalFragment extends DWFragmentCommon {
    private static final String CURRENCY = "currency";
    private String currency;
    private View view;
    private LinearLayout bankSelector;
    private LinearLayout address;
    private LinearLayout memo;
    private TextView memoLabel;
    private TextView nxtPubkeyDesc;

    private Time timeFormat = new Time();

    private ArrayList<HashMap<String, String>> historyList = new ArrayList<>();
    private SimpleAdapter historyAdapter;
    private PullToRefreshScrollView refreshScrollView;

    public static WithdrawalFragment newInstance(String currency) {
        WithdrawalFragment fragment = new WithdrawalFragment();
        Bundle args = new Bundle();
        args.putString(CURRENCY, currency);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getArguments() != null) {
            currency = getArguments().getString(CURRENCY);
        }

        view = inflater.inflate(R.layout.withdrawal_fragment, container, false);
        bankSelector = (LinearLayout) view.findViewById(R.id.withdrawal_bank_card_selector);
        address = (LinearLayout) view.findViewById(R.id.withdrawal_address);
        memo = (LinearLayout) view.findViewById(R.id.withdrawal_memo);
        memoLabel = (TextView) view.findViewById(R.id.withdrawal_memo_label);
        nxtPubkeyDesc = (TextView) view.findViewById(R.id.withdrawal_nxt_pubkey_description);

        ListView history = (ListView) view.findViewById(R.id.withdrawal_history);
        history.setFocusable(false);
        historyAdapter = new SimpleAdapter(getActivity(), historyList, R.layout.transfer_item, new String[]{
                "transfer_time", "transfer_amount", "transfer_status"}, new int[] {R.id.transfer_time, R.id.transfer_amount,
                R.id.transfer_status});
        history.setAdapter(historyAdapter);
        refreshScrollView = (PullToRefreshScrollView) view.findViewById(R.id.refreshable_view);

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
        String withdrawalDescription;
        switch (currency) {
            case "CNY":
                withdrawalDescription = getString(R.string.withdrawal_description_cny);
                setItemsVisibility(EnumSet.of(OptItem.BANK));

                Button addBankCard = (Button) view.findViewById(R.id.withdrawal_add_bank_card);

                final FragmentActivity fa = (FragmentActivity) this.getActivity();
                addBankCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AddBankCardFragment dialog = new AddBankCardFragment();
                        dialog.setPositiveButton(new AddBankCardFragment.OnClickListener() {
                            @Override
                            public void onClick(Bundle args) {
                                Log.d(this.toString(), args.getString(getString(R.string.withdrawal_abc_name)));
                            }
                        });
                        dialog.setNegativeButton(new AddBankCardFragment.OnClickListener() {
                            @Override
                            public void onClick(Bundle args) {
                                // do nothing
                            }
                        });
                        dialog.show(fa.getSupportFragmentManager(), "AddBankCardFragment");
                    }
                });
                BankCardSpinner bcSpinner = (BankCardSpinner) view.findViewById(R.id.bank_card_spinner);
                final ArrayList<String> list = new ArrayList<>();
                list.add("a");
                list.add("b");
                list.add("c");
                list.add("d");
                bcSpinner.setList(list);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                    android.R.layout.simple_spinner_item, list);
                bcSpinner.setAdapter(adapter);
                bcSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Log.d("hoss", list.get(position));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                break;
            case "BTSX":
                withdrawalDescription = getString(R.string.withdrawal_description_btsx);
                setItemsVisibility(EnumSet.of(OptItem.ADDRESS, OptItem.MEMO));
                memoLabel.setText(getString(R.string.withdrawal_memo));

                break;
            case "NXT":
                withdrawalDescription = getString(R.string.withdrawal_description_btc);
                setItemsVisibility(EnumSet.of(OptItem.ADDRESS, OptItem.MEMO, OptItem.PUBKEY_DESCRIPTION));
                memoLabel.setText(getString(R.string.withdrawal_nxt_pubkey));

                break;
            default:
                withdrawalDescription = getString(R.string.withdrawal_description_btc);
                setItemsVisibility(EnumSet.of(OptItem.ADDRESS));

                break;
        }
        TextView description = (TextView) view.findViewById(R.id.withdrawal_description);
        description.setText(String.format(withdrawalDescription, "100" + " " + currency, "1" + " " + currency));
        TextView withdrawalSumLabel = (TextView) view.findViewById(R.id.withdrawal_sum_label);
        withdrawalSumLabel.setText(String.format(getString(R.string.withdrawal_sum_label), currency));
        TextView withdrawalSum = (TextView) view.findViewById(R.id.withdrawal_sum);
        withdrawalSum.setText("31323.234123");
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

    // TODO(c): extract the common part with same name function in DepositFragment
    private void updateWithdrawalHistory() {
        String url = String.format(Constants.TRANSFER_URL, currency, App.getAccount().uid);
        Map<String, String> params = new HashMap<>();
        params.put("limit", "10");
        params.put("page", "1");
        params.put("type", "1");
        NetworkAsyncTask task = new NetworkAsyncTask(url, Constants.HttpMethod.GET)
                .setOnSucceedListener(new OnApiResponseListener())
                .setOnFailedListener(new OnApiResponseListener())
                .setRenderListener(new NetworkAsyncTask.OnPostRenderListener() {
                    @Override
                    public void onRender(NetworkRequest s) {
                        if (s.getApiStatus() != NetworkRequest.ApiStatus.SUCCEED) {
                            if (s.getApiStatus() == NetworkRequest.ApiStatus.UNAUTH) {
                                Intent intent = new Intent(WithdrawalFragment.this.getActivity(), LoginActivity.class);
                                WithdrawalFragment.this.getActivity().startActivity(intent);
                            } else {
                                Toast.makeText(getActivity(), getString(R.string.request_failed),
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            historyList.clear();
                            JSONArray jsonArray = Util.getJsonArrayByPath(s.getApiResult(), "data.items");
                            if (jsonArray != null) {
                                for (int i = 0; i < jsonArray.length(); ++i) {
                                    HashMap<String, String> fields = new HashMap<>();
                                    try {
                                        JSONObject jsonObj = jsonArray.getJSONObject(i);
                                        timeFormat.set(jsonObj.getLong("updated"));
                                        fields.put("transfer_time", timeFormat.format("%Y-%m-%d %k:%M:%S"));
                                        fields.put("transfer_amount", jsonObj.getJSONObject("amount").getString("display"));
                                        fields.put("transfer_status", getString(Util.transferStatus.get(jsonObj.getInt("status"))));
                                        historyList.add(fields);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            historyAdapter.notifyDataSetChanged();
                        }
                        refreshScrollView.onRefreshComplete();
                    }
                });
        task.execute(params);
    }

    private enum OptItem {
        BANK, ADDRESS, MEMO, PUBKEY_DESCRIPTION
    }
}
