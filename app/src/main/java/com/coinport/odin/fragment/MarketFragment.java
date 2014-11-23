package com.coinport.odin.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.coinport.odin.R;

/**
 * Created by hoss on 14-11-23.
 */
public class MarketFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.market_fragment, container, false);
    }
}
