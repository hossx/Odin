package com.coinport.odin.fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.format.Time;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.coinport.odin.App;
import com.coinport.odin.R;
import com.coinport.odin.activity.LoginActivity;
import com.coinport.odin.dialog.CustomProgressDialog;
import com.coinport.odin.library.ptr.PullToRefreshBase;
import com.coinport.odin.library.ptr.PullToRefreshScrollView;
import com.coinport.odin.network.NetworkAsyncTask;
import com.coinport.odin.network.NetworkRequest;
import com.coinport.odin.network.OnApiResponseListener;
import com.coinport.odin.util.Constants;
import com.coinport.odin.util.EncodingHandler;
import com.coinport.odin.util.Util;
import com.google.zxing.WriterException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class DepositFragment extends DWFragmentCommon {
    private static final String CURRENCY = "currency";
    private View view = null;
    private String currency;
    private LinearLayout depositCnyInfo;
    private LinearLayout depositInfo;
    private Time timeFormat = new Time();

    private static final String QQ_URI_HEADER = "mqqwpa:";
    private static Map<String, String> uriHeader = new HashMap<>();


    private TextView address;
    private TextView alias;
    private TextView memo;
    private TextView nxtPubkey;
    private ImageView qrView;
    private TextView link;

    private ArrayList<HashMap<String, String>> historyList = new ArrayList<>();
    private SimpleAdapter historyAdapter;
    private PullToRefreshScrollView refreshScrollView;
    private CustomProgressDialog cpd = null;

    static {
        uriHeader.put("BTC", "bitcoin:");
        uriHeader.put("LTC", "litecoin:");
        uriHeader.put("DOGE", "dogecoin:");
    }

    public DepositFragment() {}

    public static DepositFragment newInstance(String currency) {
        DepositFragment fragment = new DepositFragment();
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

        view = inflater.inflate(R.layout.deposit_fragment, container, false);
        depositInfo = (LinearLayout) view.findViewById(R.id.deposit_info);
        depositCnyInfo = (LinearLayout) view.findViewById(R.id.deposit_cny_info);
        address = (TextView) view.findViewById(R.id.crypto_currency_address);
        qrView = (ImageView) view.findViewById(R.id.qr_image);
        alias = (TextView) view.findViewById(R.id.deposit_alias);
        memo = (TextView) view.findViewById(R.id.deposit_memo);
        nxtPubkey = (TextView) view.findViewById(R.id.deposit_nxt_pubkey);

        ListView history = (ListView) view.findViewById(R.id.deposit_history);
        history.setFocusable(false);
        historyAdapter = new SimpleAdapter(getActivity(), historyList, R.layout.transfer_item, new String[]{
            "transfer_time", "transfer_amount", "transfer_status"}, new int[] {R.id.transfer_time, R.id.transfer_amount,
            R.id.transfer_status});
        history.setAdapter(historyAdapter);
        refreshScrollView = (PullToRefreshScrollView) view.findViewById(R.id.refreshable_view);
        refreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                updateDepositInfo(true);
            }
        });

        updateDepositInfo(false);
        return view;
    }

    @Override
    public void setCurrency(String currency) {
        this.currency = currency;
        if (view != null)
            updateDepositInfo(false);
    }

    private void updateDepositInfo(boolean isPull) {
        if (!isPull) {
            cpd = CustomProgressDialog.createDialog(getActivity());
            cpd.show();
        }
        switch (currency) {
            case "CNY":
                updateDepositCnyInfo();
                break;
            case "BTSX":
                updateDepositBtsxInfo();
                break;
            case "NXT":
                updateDepositNxtInfo();
                break;
            case "XRP":
                updateDepositXrpInfo();
                break;
            case "GOOC":
                updateDepositGoocInfo();
                break;
            default:
                updateDepositBtcInfo();
                break;
        }
    }

    private void renderLinkQrcode(String address) {
        link = (TextView) view.findViewById(R.id.open_bitcoin_link);
        if (uriHeader.containsKey(currency)) {
            String baseUri = uriHeader.get(currency);
            PackageManager pm = getActivity().getPackageManager();
            Intent testIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(baseUri));
            if (testIntent.resolveActivity(pm) != null) {
                link.setText(Html.fromHtml("<a href=\"" + baseUri + address + "\">" +
                        getString(R.string.deposit_link) + "</a>"));
                link.setMovementMethod(LinkMovementMethod.getInstance());
                link.setVisibility(View.VISIBLE);
            } else {
                link.setVisibility(View.GONE);
            }
        } else {
            link.setVisibility(View.GONE);
        }

        try {
            Bitmap qrCodeBitmap = EncodingHandler.createQRCode(address, 350);
            qrView.setImageBitmap(qrCodeBitmap);
            qrView.setVisibility(View.VISIBLE);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private void setItemsVisibility(EnumSet<OptItem> opts) {
        alias.setVisibility(View.GONE);
        memo.setVisibility(View.GONE);
        nxtPubkey.setVisibility(View.GONE);
        if (qrView != null) qrView.setVisibility(View.GONE);
        if (link != null) link.setVisibility(View.GONE);
        if (opts.contains(OptItem.ALIAS))
            alias.setVisibility(View.VISIBLE);
        if (opts.contains(OptItem.MEMO))
            memo.setVisibility(View.VISIBLE);
        if (opts.contains(OptItem.NXT_PUBKEY))
            nxtPubkey.setVisibility(View.VISIBLE);
        if (opts.contains(OptItem.QR_CODE))
            if (qrView != null) qrView.setVisibility(View.VISIBLE);
        if (opts.contains(OptItem.LINK))
            if (link != null) link.setVisibility(View.VISIBLE);
    }

    private void updateAddress(final NetworkAsyncTask.OnPostRenderListener listener) {
        address.setVisibility(View.VISIBLE);
        qrView.setVisibility(View.VISIBLE);
        String url = String.format(Constants.DEPOSIT_ADDRESS_URL, currency, App.getAccount().uid);
        NetworkAsyncTask task = new NetworkAsyncTask(url, Constants.HttpMethod.GET)
                .setOnSucceedListener(new OnApiResponseListener())
                .setOnFailedListener(new OnApiResponseListener())
                .setRenderListener(new NetworkAsyncTask.OnPostRenderListener() {

                    @Override
                    public void onRender(NetworkRequest s) {
                        if (!isAdded())
                            return;
                        if (s.getApiStatus() != NetworkRequest.ApiStatus.SUCCEED) {
                            address.setVisibility(View.GONE);
                            qrView.setVisibility(View.GONE);
                            if (s.getApiStatus() == NetworkRequest.ApiStatus.UNAUTH) {
                                Intent intent = new Intent(DepositFragment.this.getActivity(), LoginActivity.class);
                                DepositFragment.this.getActivity().startActivity(intent);
                            } else {
                                Toast.makeText(getActivity(), getString(R.string.request_failed),
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            listener.onRender(s);
                        }
                    }
                });
        task.execute();
    }

    private void updateDepositBtcInfo() {
        depositInfo.setVisibility(View.VISIBLE);
        depositCnyInfo.setVisibility(View.GONE);
        setItemsVisibility(EnumSet.of(OptItem.QR_CODE, OptItem.LINK));

        updateAddress(new NetworkAsyncTask.OnPostRenderListener() {
            @Override
            public void onRender(NetworkRequest s) {
                if (!isAdded())
                    return;
                TextView tv = (TextView) view.findViewById(R.id.deposit_header);
                tv.setText(String.format(getString(R.string.deposit_info), currency));
                JSONObject obj = Util.getJsonObjectByPath(s.getApiResult(), "data");
                try {
                    if (obj != null && !obj.getString(currency).equals("")) {
                        String addressStr = obj.getString(currency);
                        address.setText(addressStr);
                        renderLinkQrcode(addressStr);
                    } else {
                        address.setVisibility(View.GONE);
                        qrView.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        updateDepositHistory();
    }

    private void updateDepositCnyInfo() {
        depositInfo.setVisibility(View.GONE);
        depositCnyInfo.setVisibility(View.VISIBLE);
        PackageManager pm = getActivity().getPackageManager();
        Intent testIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(QQ_URI_HEADER));
        final boolean supportsQQ = testIntent.resolveActivity(pm) != null;

        ListView lv = (ListView) view.findViewById(R.id.agent_cards);
        lv.setFocusable(false);
        ArrayList<HashMap<String, String>> acList = new ArrayList<>();
        JSONArray jsonArray = Util.getJsonArrayFromFile(getActivity(), "agent_cards.json");
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); ++i) {
                HashMap<String, String> fields = new HashMap<>();
                try {
                    JSONObject jsonObj = jsonArray.getJSONObject(i);
                    fields.put("agent_card_nick_name", jsonObj.getString("nn"));
                    fields.put("agent_card_name", jsonObj.getString("n"));
                    fields.put("agent_card_qq", jsonObj.getString("q"));
                    acList.add(fields);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        SimpleAdapter sa = new SimpleAdapter(getActivity(), acList, R.layout.agent_card, new String[]{
            "agent_card_nick_name", "agent_card_name", "agent_card_qq"},
            new int[]{R.id.agent_card_nick_name, R.id.agent_card_name, R.id.agent_card_qq}) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                if (supportsQQ) {
                    TextView qqTv = (TextView) view.findViewById(R.id.agent_card_qq);
                    qqTv.setText(Html.fromHtml("<a href=\"mqqwpa://im/chat?chat_type=wpa&uin=" + qqTv.getText() + "\">"
                        + qqTv.getText() + "</a>"));
                    qqTv.setMovementMethod(LinkMovementMethod.getInstance());
                    qqTv.setBackgroundResource(R.drawable.cancel_order_button);
                }
                return view;
            }
        };
        lv.setAdapter(sa);

        updateDepositHistory();
    }

    private void updateDepositBtsxInfo() {
        depositInfo.setVisibility(View.VISIBLE);
        depositCnyInfo.setVisibility(View.GONE);
        setItemsVisibility(EnumSet.of(OptItem.ALIAS, OptItem.MEMO, OptItem.QR_CODE, OptItem.LINK));

        TextView tv = (TextView) view.findViewById(R.id.deposit_header);
        tv.setText(String.format(getString(R.string.deposit_info), currency));

        address.setText(getString(R.string.btsx_address));
        address.setVisibility(View.VISIBLE);
        alias.setText(String.format(getString(R.string.deposit_alias_name), getString(R.string.btsx_alias)));
        memo.setText(String.format(getString(R.string.deposit_memo), App.getAccount().uid));
        renderLinkQrcode(getString(R.string.btsx_address));
        updateDepositHistory();
    }

    private void updateDepositGoocInfo() {
        depositInfo.setVisibility(View.VISIBLE);
        depositCnyInfo.setVisibility(View.GONE);
        setItemsVisibility(EnumSet.of(OptItem.MEMO, OptItem.QR_CODE, OptItem.LINK));

        TextView tv = (TextView) view.findViewById(R.id.deposit_header);
        tv.setText(String.format(getString(R.string.deposit_info), currency));

        address.setText(R.string.gooc_address);
        address.setVisibility(View.VISIBLE);
        memo.setText(String.format(getString(R.string.deposit_comment), App.getAccount().uid));
        renderLinkQrcode(getString(R.string.gooc_address));
        updateDepositHistory();
    }

    private void updateDepositNxtInfo() {
        depositInfo.setVisibility(View.VISIBLE);
        depositCnyInfo.setVisibility(View.GONE);
        setItemsVisibility(EnumSet.of(OptItem.ALIAS, OptItem.NXT_PUBKEY, OptItem.QR_CODE, OptItem.LINK));

        alias.setVisibility(View.GONE);
        nxtPubkey.setVisibility(View.GONE);
        updateAddress(new NetworkAsyncTask.OnPostRenderListener() {
            @Override
            public void onRender(NetworkRequest s) {
                if (!isAdded())
                    return;
                TextView tv = (TextView) view.findViewById(R.id.deposit_header);
                tv.setText(String.format(getString(R.string.deposit_info), currency));
                JSONObject obj = Util.getJsonObjectByPath(s.getApiResult(), "data");
                try {
                    if (obj != null && !obj.getString(currency).equals("")) {
                        String addressStr = obj.getString(currency);
                        String[] segments = addressStr.split("//");
                        if (segments.length < 3) {
                            address.setVisibility(View.GONE);
                            qrView.setVisibility(View.GONE);
                            return;
                        }
                        address.setText(segments[0]);
                        alias.setText(String.format(getString(R.string.deposit_alias_name), segments[1]));
                        alias.setVisibility(View.VISIBLE);
                        nxtPubkey.setText(String.format(getString(R.string.deposit_nxt_pubkey), segments[2]));
                        nxtPubkey.setVisibility(View.VISIBLE);
                        renderLinkQrcode(segments[0]);
                    } else {
                        address.setVisibility(View.GONE);
                        qrView.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        updateDepositHistory();
    }

    private void updateDepositXrpInfo() {
        depositInfo.setVisibility(View.VISIBLE);
        depositCnyInfo.setVisibility(View.GONE);
        setItemsVisibility(EnumSet.of(OptItem.MEMO, OptItem.QR_CODE, OptItem.LINK));

        TextView tv = (TextView) view.findViewById(R.id.deposit_header);
        tv.setText(String.format(getString(R.string.deposit_info), currency));

        address.setText(getString(R.string.xrp_address));
        address.setVisibility(View.VISIBLE);
        qrView = (ImageView) view.findViewById(R.id.qr_image);
        memo.setText(String.format(getString(R.string.deposit_tag), App.getAccount().uid));
        renderLinkQrcode(getString(R.string.xrp_address));
        updateDepositHistory();
    }

    private void updateDepositHistory() {
        String url = String.format(Constants.TRANSFER_URL, currency, App.getAccount().uid);
        Map<String, String> params = new HashMap<>();
        params.put("limit", "10");
        params.put("page", "1");
        params.put("type", "0");
        NetworkAsyncTask task = new NetworkAsyncTask(url, Constants.HttpMethod.GET)
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
                                Intent intent = new Intent(DepositFragment.this.getActivity(), LoginActivity.class);
                                DepositFragment.this.getActivity().startActivity(intent);
                            } else {
                                if (isAdded())
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
        ALIAS, MEMO, NXT_PUBKEY, QR_CODE, LINK
    }
}
