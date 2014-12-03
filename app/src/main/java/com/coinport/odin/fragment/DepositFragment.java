package com.coinport.odin.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.coinport.odin.R;

/**
 * Created by hoss on 14-12-3.
 */
public class DepositFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.deposit_fragment, container, false);
        return view;
    }
}
