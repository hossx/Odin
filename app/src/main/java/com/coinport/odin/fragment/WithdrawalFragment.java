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
public class WithdrawalFragment extends Fragment {
    private String currency;

    public WithdrawalFragment(String currency) {
        this.currency = currency;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.withdrawal_fragment, container, false);
        return view;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
