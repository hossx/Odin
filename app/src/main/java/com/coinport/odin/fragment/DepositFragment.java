package com.coinport.odin.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.coinport.odin.R;
import com.coinport.odin.util.EncodingHandler;
import com.google.zxing.WriterException;

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

    private static Map<String, String> uriHeader = new HashMap<String, String>();

    static {
        uriHeader.put("BTC", "bitcoin:x");
        uriHeader.put("LTC", "litecoin:x");
        uriHeader.put("DOGE", "dogecoin:x");
    }

    public DepositFragment(String currency) {
        this.currency = currency;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.deposit_fragment, container, false);
        updateDepositInfo();
        return view;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
        if (view != null)
            updateDepositInfo();
    }

    private void updateDepositInfo() {
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

        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, items);
        ListView lv = (ListView) view.findViewById(R.id.deposit_history);
        lv.setFocusable(false);
        lv.setAdapter(adapter);
    }
}