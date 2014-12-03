package com.coinport.odin.fragment;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.coinport.odin.R;
import com.coinport.odin.util.EncodingHandler;
import com.google.zxing.WriterException;

/**
 * Created by hoss on 14-12-3.
 */
public class DepositFragment extends Fragment {
    private ImageView qrView;
    private String currency;

    public DepositFragment(String currency) {
        this.currency = currency;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.deposit_fragment, container, false);
        TextView tv = (TextView) view.findViewById(R.id.deposit_header);
        tv.setText(String.format(getActivity().getString(R.string.deposit_info), currency));
        qrView = (ImageView) view.findViewById(R.id.qr_image);
        try {
            Bitmap qrCodeBitmap = EncodingHandler.createQRCode("188puwQGf5e66wTHCpFaKmLY2JXcdTkHgg", 1000);
            qrView.setImageBitmap(qrCodeBitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return view;
    }
}
