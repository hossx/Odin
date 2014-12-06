package com.coinport.odin.fragment;

import android.app.Fragment;
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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.coinport.odin.R;
import com.coinport.odin.util.EncodingHandler;
import com.coinport.odin.util.Util;
import com.google.zxing.WriterException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hoss on 14-12-3.
 */
public class DepositFragment extends Fragment {
    private View view = null;
    private ImageView qrView;
    private String currency;
    private LinearLayout depositCnyInfo;
    private LinearLayout depositInfo;
    private Time timeFormat = new Time();

    private static final String QQ_URI_HEADER = "mqqwpa:x";
    private static Map<String, String> uriHeader = new HashMap<String, String>();

    private static Map<Integer, Integer> transferStatus = new HashMap<>();

    static {
        uriHeader.put("BTC", "bitcoin:x");
        uriHeader.put("LTC", "litecoin:x");
        uriHeader.put("DOGE", "dogecoin:x");

        transferStatus.put(0, R.string.deposit_pending);
        transferStatus.put(1, R.string.deposit_processing);
        transferStatus.put(2, R.string.deposit_processed);
        transferStatus.put(3, R.string.deposit_processed);
        transferStatus.put(4, R.string.deposit_succeed);
        transferStatus.put(5, R.string.deposit_failed);
        transferStatus.put(6, R.string.deposit_succeed);
        transferStatus.put(7, R.string.deposit_succeed);
        transferStatus.put(8, R.string.deposit_cancelled);
        transferStatus.put(9, R.string.deposit_rejected);
        transferStatus.put(10, R.string.deposit_failed);
        transferStatus.put(11, R.string.deposit_processing);
        transferStatus.put(12, R.string.deposit_failed);
        transferStatus.put(13, R.string.deposit_failed);
        transferStatus.put(14, R.string.deposit_failed);
        transferStatus.put(15, R.string.deposit_failed);
        transferStatus.put(16, R.string.deposit_failed);
    }

    public DepositFragment(String currency) {
        this.currency = currency;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.deposit_fragment, container, false);
        depositInfo = (LinearLayout) view.findViewById(R.id.deposit_info);
        depositCnyInfo = (LinearLayout) view.findViewById(R.id.deposit_cny_info);
        updateDepositInfo();
        return view;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
        if (view != null)
            updateDepositInfo();
    }

    private void updateDepositInfo() {
        if (currency.equals("CNY")) {
            updateDepositCnyInfo();
        } else if (currency.equals("BTSX")) {
            updateDepositBtsxInfo();
        } else if (currency.equals("NXT")) {
            updateDepositNxtInfo();
        } else if (currency.equals("XRP")) {
            updateDepositXrpInfo();
        } else {
            updateDepositBtcInfo();
        }
    }

    private void updateDepositBtcInfo() {
        depositInfo.setVisibility(View.VISIBLE);
        depositCnyInfo.setVisibility(View.GONE);

        TextView tv = (TextView) view.findViewById(R.id.deposit_header);
        tv.setText(String.format(getActivity().getString(R.string.deposit_info), currency));

        TextView link = (TextView) view.findViewById(R.id.open_bitcoin_link);
        if (uriHeader.containsKey(currency)) {
            String baseUri = uriHeader.get(currency);
            PackageManager pm = getActivity().getPackageManager();
            Intent testIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(baseUri));
            if (testIntent.resolveActivity(pm) != null) {
                link.setText(Html.fromHtml("<a href=\"bitcoin:1C1ML3Jt1zNdLQ3e7KKZ6Ar8BMH2gYgQHC\">" +
                        getString(R.string.deposit_link) + "</a>"));
                // mqqwpa://im/chat?chat_type=wpa&uin=501863587
                link.setMovementMethod(LinkMovementMethod.getInstance());
                link.setVisibility(View.VISIBLE);
            } else {
                link.setVisibility(View.GONE);
            }
        } else {
            link.setVisibility(View.GONE);
        }

        qrView = (ImageView) view.findViewById(R.id.qr_image);
        try {
            Bitmap qrCodeBitmap = EncodingHandler.createQRCode("188puwQGf5e66wTHCpFaKmLY2JXcdTkHgg", 350);
            qrView.setImageBitmap(qrCodeBitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

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
        ArrayList<HashMap<String, String>> acList = new ArrayList<HashMap<String, String>>();
        JSONArray jsonArray = Util.getJsonArrayFromFile(getActivity(), "agent_cards.json");
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); ++i) {
                HashMap<String, String> fields = new HashMap<String, String>();
                try {
                    JSONObject jsonObj = jsonArray.getJSONObject(i);
                    fields.put("agent_card_nick_name", jsonObj.getString("nn"));
                    fields.put("agent_card_name", jsonObj.getString("n"));
                    fields.put("agent_card_bank", jsonObj.getString("b"));
                    fields.put("agent_card_account", jsonObj.getString("a"));
                    fields.put("agent_card_qq", jsonObj.getString("q"));
                    acList.add(fields);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        SimpleAdapter sa = new SimpleAdapter(getActivity(), acList, R.layout.agent_card, new String[]{
            "agent_card_nick_name", "agent_card_name", "agent_card_bank", "agent_card_account", "agent_card_qq"},
            new int[]{R.id.agent_card_nick_name, R.id.agent_card_name, R.id.agent_card_bank, R.id.agent_card_account,
                R.id.agent_card_qq}) {
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

        updateDepositHistory();
    }

    private void updateDepositNxtInfo() {
        depositInfo.setVisibility(View.VISIBLE);
        depositCnyInfo.setVisibility(View.GONE);

        updateDepositHistory();
    }

    private void updateDepositXrpInfo() {
        depositInfo.setVisibility(View.VISIBLE);
        depositCnyInfo.setVisibility(View.GONE);

        updateDepositHistory();
    }

    private void updateDepositHistory() {
        ListView lv = (ListView) view.findViewById(R.id.deposit_history);
        lv.setFocusable(false);

        ArrayList<HashMap<String, String>> dhList = new ArrayList<HashMap<String, String>>();
        JSONArray jsonArray = Util.getJsonArrayFromFile(getActivity(), "deposit_history_mock.json");
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); ++i) {
                HashMap<String, String> fields = new HashMap<String, String>();
                try {
                    JSONObject jsonObj = jsonArray.getJSONObject(i);
                    timeFormat.set(jsonObj.getLong("updated"));
                    fields.put("deposit_time", timeFormat.format("%Y-%m-%d %k:%M:%S"));
                    fields.put("deposit_amount", jsonObj.getJSONObject("amount").getString("display"));
                    fields.put("deposit_status", getString(transferStatus.get(jsonObj.getInt("status"))));
                    dhList.add(fields);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        SimpleAdapter adapter = new SimpleAdapter(getActivity(), dhList, R.layout.transfer_item, new String[]{
            "deposit_time", "deposit_amount", "deposit_status"}, new int[] {R.id.deposit_time, R.id.deposit_amount,
            R.id.deposit_status});
        lv.setAdapter(adapter);
    }
}
