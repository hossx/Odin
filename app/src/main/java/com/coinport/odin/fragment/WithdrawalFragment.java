package com.coinport.odin.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.coinport.odin.App;
import com.coinport.odin.R;
import com.coinport.odin.activity.UserVerifyActivity;
import com.coinport.odin.dialog.CustomProgressDialog;
import com.coinport.odin.layout.BankCardSpinner;
import com.coinport.odin.library.ptr.PullToRefreshBase;
import com.coinport.odin.library.ptr.PullToRefreshScrollView;
import com.coinport.odin.network.NetworkAsyncTask;
import com.coinport.odin.network.NetworkRequest;
import com.coinport.odin.network.OnApiResponseListener;
import com.coinport.odin.obj.AccountInfo;
import com.coinport.odin.util.Constants;
import com.coinport.odin.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class WithdrawalFragment extends DWFragmentCommon implements View.OnClickListener {
    private static final String CURRENCY = "currency";
    private String currency;
    private View view;
    private LinearLayout bankSelector;
    private LinearLayout address;

    private LinearLayout realnameHint;
    private LinearLayout memo;
    private TextView memoLabel;
    private TextView nxtPubkeyDesc;

    private Time timeFormat = new Time();

    private ArrayList<HashMap<String, String>> historyList = new ArrayList<>();
    private SimpleAdapter historyAdapter;
    private PullToRefreshScrollView refreshScrollView;

    private String limit = "", fee = "";
    private double asset = 0.0;
    private String withdrawalEmailUUID = "";

    private ArrayList<String> cardList = new ArrayList<>();
    private ArrayAdapter<String> cardAdapter;
    private BankCardSpinner bcSpinner;

    private CustomProgressDialog cpd = null;

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
        realnameHint = (LinearLayout) view.findViewById(R.id.verification_hint);
        refreshScrollView = (PullToRefreshScrollView) view.findViewById(R.id.refreshable_view);
        refreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                updateWithdrawalInfo(true);
            }
        });

        Button getWithdrawalEmailCode = (Button) view.findViewById(R.id.get_withdrawal_email_code);
        getWithdrawalEmailCode.setOnClickListener(this);
        Button withdrawalBtn = (Button) view.findViewById(R.id.withdrawal_action);
        withdrawalBtn.setOnClickListener(this);

        bcSpinner = (BankCardSpinner) view.findViewById(R.id.bank_card_spinner);
        bcSpinner.setList(cardList);
        cardAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, cardList);
        bcSpinner.setAdapter(cardAdapter);
        bcSpinner.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cardList.isEmpty()) {
                    cardList.add(getString(R.string.add_bc_condition));
                    bcSpinner.setEnabled(false);
                }
                cardAdapter.notifyDataSetChanged();
            }
        });

        View unsupportedHint = view.findViewById(R.id.unsupported_hint);
        if (App.getAccount().needGoogleAuth() || App.getAccount().needSms()) {
            unsupportedHint.setVisibility(View.VISIBLE);
            realnameHint.setVisibility(View.GONE);
            refreshScrollView.setVisibility(View.GONE);
        } else {
            unsupportedHint.setVisibility(View.GONE);
            realnameHint.setVisibility(View.GONE);
            refreshScrollView.setVisibility(View.VISIBLE);
            updateWithdrawalInfo(false);
        }
        return view;
    }

    @Override
    public void setCurrency(String currency) {
        this.currency = currency;
        if (view != null)
            updateWithdrawalInfo(false);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser && view != null)
            view.clearFocus();
    }

    private void updateWithdrawalInfo(boolean isPull) {
        if (currency.equals("CNY") && (App.getAccount().realname == null || App.getAccount().realname.equals(""))) {
            refreshScrollView.setVisibility(View.GONE);
            realnameHint.setVisibility(View.VISIBLE);
            Button goToVerifyBtn = (Button) view.findViewById(R.id.realname_verify);
            goToVerifyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("fromWDPage", true);
                    intent.setClass(getActivity(), UserVerifyActivity.class);
                    startActivityForResult(intent, 0);
                }
            });
            return;
        }
        if (!isPull) {
            cpd = CustomProgressDialog.createDialog(getActivity());
            cpd.show();
        }
        realnameHint.setVisibility(View.GONE);
        refreshScrollView.setVisibility(View.VISIBLE);
        fetchFeeRule();
        fetchAsset(0);
        updateWithdrawalHistory(0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 0 && view != null) {
            updateWithdrawalInfo(false);
        }
    }

    private void fetchAsset(long delay) {
        TextView withdrawalSumLabel = (TextView) view.findViewById(R.id.withdrawal_sum_label);
        withdrawalSumLabel.setText(String.format(getString(R.string.withdrawal_sum_label), currency));
        final TextView withdrawalSum = (TextView) view.findViewById(R.id.withdrawal_sum);
        AccountInfo ai = App.getAccount();
        String url = String.format(Constants.ASSET_URL, ai.uid);
        NetworkAsyncTask task = new NetworkAsyncTask(url, Constants.HttpMethod.GET, delay)
                .setOnSucceedListener(new OnApiResponseListener())
                .setOnFailedListener(new OnApiResponseListener())
                .setRenderListener(new NetworkAsyncTask.OnPostRenderListener() {
                    @Override
                    public void onRender(NetworkRequest s) {
                        if (!isAdded())
                            return;
                        withdrawalSum.setText("0.0");
                        if (s.getApiStatus() != NetworkRequest.ApiStatus.SUCCEED)
                            return;
                        try {
                            JSONObject assetJson = Util.getJsonObjectByPath(
                                    s.getApiResult(), "data.accounts." + currency);
                            if (assetJson == null)
                                asset = 0;
                            else {
                                asset = assetJson.getJSONObject("available").getDouble("value");
                                withdrawalSum.setText(Util.displayDouble(asset, 4));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        task.execute();
    }

    private void fetchBankCards(long delay) {
        Button addBankCard = (Button) view.findViewById(R.id.withdrawal_add_bank_card);
        addBankCard.setOnClickListener(WithdrawalFragment.this);

        NetworkAsyncTask task = new NetworkAsyncTask(Constants.BANK_CARD_URL, Constants.HttpMethod.GET, delay)
                .setOnSucceedListener(new OnApiResponseListener())
                .setOnFailedListener(new OnApiResponseListener())
                .setRenderListener(new NetworkAsyncTask.OnPostRenderListener() {
                    @Override
                    public void onRender(NetworkRequest s) {
                        if (!isAdded())
                            return;
                        if (s.getApiStatus() != NetworkRequest.ApiStatus.SUCCEED) {
                            if (s.getApiStatus() == NetworkRequest.ApiStatus.UNAUTH) {
                                Intent intent = Util.toLoginFromAuthFail(WithdrawalFragment.this.getActivity(), true);
                                WithdrawalFragment.this.getActivity().startActivity(intent);
                            } else {
                                Toast.makeText(getActivity(), getString(R.string.request_failed), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            JSONArray jsonArray = Util.getJsonArrayByPath(s.getApiResult(), "data");
                            if (jsonArray == null || jsonArray.length() == 0) {
                                cardList.clear();
                                cardList.add(getString(R.string.add_bc_condition));
                                cardAdapter.notifyDataSetChanged();
                                bcSpinner.setEnabled(false);
                            } else {
                                bcSpinner.setEnabled(true);
                                cardList.clear();
                                for (int i = 0; i < jsonArray.length(); ++i) {
                                    try {
                                        JSONObject card = jsonArray.getJSONObject(i);
                                        cardList.add(String.format("%1$s|%2$s|%3$s|%4$s", card.getString("ownerName"),
                                                card.getString("cardNumber"), card.getString("bankName"),
                                                Util.getStringFromJson(card, "branchBankName")));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                cardAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
        task.execute();
    }

    private void fetchFeeRule() {
        if (currency.equals("CNY")) {
            String withdrawalDescription = getString(R.string.withdrawal_description_cny);
            setItemsVisibility(EnumSet.of(OptItem.BANK));
            limit = "2";
            fee = getString(R.string.withdrawal_cny_fee_description);
            TextView description = (TextView) view.findViewById(R.id.withdrawal_description);
            description.setText(String.format(withdrawalDescription, limit + " " + currency, fee + " " + currency));
            fetchBankCards(0);
        } else {
            NetworkAsyncTask task = new NetworkAsyncTask(Constants.FEE_URL, Constants.HttpMethod.GET)
                    .setOnSucceedListener(new OnApiResponseListener())
                    .setOnFailedListener(new OnApiResponseListener())
                    .setRenderListener(new NetworkAsyncTask.OnPostRenderListener() {
                        @Override
                        public void onRender(NetworkRequest s) {
                            if (!isAdded())
                                return;
                            if (s.getApiStatus() != NetworkRequest.ApiStatus.SUCCEED) {
                                Toast.makeText(getActivity(), getString(R.string.request_failed),
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }
                            JSONObject feeObj = null;
                            feeObj = Util.getJsonObjectByPath(s.getApiResult(), "data." + currency);
                            String withdrawalDescription = "";
                            if (feeObj != null) {
                                try {
                                    limit = feeObj.getString("l");
                                    fee = feeObj.getString("f");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            switch (currency) {
                                case "CNY":
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
                            description.setText(String.format(withdrawalDescription, limit + " " + currency,
                                    fee + " " + currency));
                        }
                    });
            task.execute();
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

    // TODO(c): extract the common part with same name function in DepositFragment
    private void updateWithdrawalHistory(long delay) {
        String url = String.format(Constants.TRANSFER_URL, currency, App.getAccount().uid);
        Map<String, String> params = new HashMap<>();
        params.put("limit", "10");
        params.put("page", "1");
        params.put("type", "1");
        NetworkAsyncTask task = new NetworkAsyncTask(url, Constants.HttpMethod.GET, delay)
                .setOnSucceedListener(new OnApiResponseListener())
                .setOnFailedListener(new OnApiResponseListener())
                .setRenderListener(new NetworkAsyncTask.OnPostRenderListener() {
                    @Override
                    public void onRender(NetworkRequest s) {
                        if (cpd != null) {
                            cpd.dismiss();
                            cpd = null;
                        }
                        if (!isAdded())
                            return;
                        if (s.getApiStatus() != NetworkRequest.ApiStatus.SUCCEED) {
                            if (s.getApiStatus() == NetworkRequest.ApiStatus.UNAUTH) {
                                Intent intent = Util.toLoginFromAuthFail(WithdrawalFragment.this.getActivity(), true);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.get_withdrawal_email_code:
                Util.countdownButton((Button) v);
                NetworkAsyncTask task = new NetworkAsyncTask(Constants.EMAIL_CODE_URL, Constants.HttpMethod.GET)
                        .setOnSucceedListener(new OnApiResponseListener())
                        .setOnFailedListener(new OnApiResponseListener())
                        .setRenderListener(new NetworkAsyncTask.OnPostRenderListener() {
                            @Override
                            public void onRender(NetworkRequest s) {
                                if (!isAdded())
                                    return;
                                if (s.getApiStatus() != NetworkRequest.ApiStatus.SUCCEED) {
                                    if (s.getApiStatus() == NetworkRequest.ApiStatus.UNAUTH) {
                                        Intent intent = Util.toLoginFromAuthFail(WithdrawalFragment.this.getActivity(), true);
                                        WithdrawalFragment.this.getActivity().startActivity(intent);
                                    } else {
                                        Toast.makeText(getActivity(), getString(R.string.request_failed),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    return;
                                }
                                try {
                                    withdrawalEmailUUID = s.getApiResult().getString("data");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                task.execute();
                break;
            case R.id.withdrawal_add_bank_card:
                AddBankCardFragment dialog = new AddBankCardFragment();
                dialog.setPositiveButton(new AddBankCardFragment.OnClickListener() {
                    @Override
                    public void onClick(Bundle args) {
                        if (!bcSpinner.isEnabled()) {
                            cardList.clear();
                            bcSpinner.setEnabled(true);
                        }
                        cardList.add(String.format("%1$s|%2$s|%3$s|%4$s", args.getString("ownerName"),
                                args.getString("cardNumber"), args.getString("bankName"),
                                args.getString("branchBankName")));
                        cardAdapter.notifyDataSetChanged();
//                        if (args.getString("succeed").equals("true")) {
//                            fetchBankCards(4000);
//                        }
                    }
                });
                dialog.setNegativeButton(new AddBankCardFragment.OnClickListener() {
                    @Override
                    public void onClick(Bundle args) {
                        // do nothing
                    }
                });
                dialog.show(((FragmentActivity) getActivity()).getSupportFragmentManager(), "AddBankCardFragment");
                break;
            case R.id.withdrawal_action:
                String amountStr = "0.0", addressStr = "", memoStr = "", publickKeyStr = "";
                amountStr = ((EditText) view.findViewById(R.id.withdrawal_amount_edit)).getText().toString();
                if (amountStr.equals(""))
                    amountStr = "0.0";
                double amount = Util.s2d(amountStr);
                if (amount > asset) {
                    Toast.makeText(getActivity(), getString(R.string.withdrawal_not_enough),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (amount < Util.s2d(limit)) {
                    Toast.makeText(getActivity(), getString(R.string.withdrawal_under_limit),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (currency.equals("CNY")) {
                    addressStr = ((BankCardSpinner) view.findViewById(R.id.bank_card_spinner)).getSelectedItem().toString();
                } else {
                    addressStr = ((EditText) view.findViewById(R.id.withdrawal_address_edit)).getText().toString();
                }
                if (currency.equals("NXT")) {
                    publickKeyStr = ((EditText) view.findViewById(R.id.withdrawal_memo_edit)).getText().toString();
                } else {
                    memoStr = ((EditText) view.findViewById(R.id.withdrawal_memo_edit)).getText().toString();
                }
                String emailCode = ((EditText) view.findViewById(R.id.withdrawal_emailcode_edit)).getText().toString();
                if (amountStr.equals("0.0") || addressStr.equals("") || emailCode.equals("")) {
                    Toast.makeText(getActivity(), getString(R.string.need_withdrawal_params),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                Map<String, String> params = new HashMap<>();
                params.put("currency", currency);
                params.put("amount", amountStr);
                params.put("address", addressStr);
                params.put("memo", memoStr);
                params.put("publicKey", publickKeyStr);
                params.put("emailuuid", withdrawalEmailUUID);
                params.put("emailcode", emailCode);

                NetworkAsyncTask withdrawalTask = new NetworkAsyncTask(Constants.WITHDRAWAL_URL,
                        Constants.HttpMethod.POST)
                        .setOnSucceedListener(new OnApiResponseListener())
                        .setOnFailedListener(new OnApiResponseListener())
                        .setRenderListener(new NetworkAsyncTask.OnPostRenderListener() {
                            @Override
                            public void onRender(NetworkRequest s) {
                                if (!isAdded())
                                    return;
                                if (s.getApiStatus() != NetworkRequest.ApiStatus.SUCCEED) {
                                    if (s.getApiStatus() == NetworkRequest.ApiStatus.UNAUTH) {
                                        Intent intent = Util.toLoginFromAuthFail(WithdrawalFragment.this.getActivity(), true);
                                        WithdrawalFragment.this.getActivity().startActivity(intent);
                                    } else if (s.getApiStatus() == NetworkRequest.ApiStatus.INTERNAL_ERROR) {
                                        Toast.makeText(getActivity(), App.getErrorMessage(s.getApiCode()),
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getActivity(), getString(R.string.request_failed),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    return;
                                } else {
                                    Toast.makeText(getActivity(), getString(R.string.withdrawal_succeed),
                                            Toast.LENGTH_SHORT).show();
                                }
                                fetchAsset(4000);
                                updateWithdrawalHistory(4000);
                            }
                        });
                withdrawalTask.execute(params);
                break;
        }
    }

    private enum OptItem {
        BANK, ADDRESS, MEMO, PUBKEY_DESCRIPTION
    }
}
