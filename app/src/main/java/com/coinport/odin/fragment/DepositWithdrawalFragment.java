package com.coinport.odin.fragment;

import android.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.coinport.odin.R;

/**
 * Created by hoss on 14-11-23.
 */
public class DepositWithdrawalFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.deposit_withdrawal_fragment, container, false);
//        ActionBar actionBar = getActivity().getActionBar();
//        if (actionBar != null)
//            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
//        Spinner currencySelector = (Spinner) view.findViewById(R.id.currency_selector);
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.currency_array,
//            android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        currencySelector.setAdapter(adapter);
        return view;
    }
}
