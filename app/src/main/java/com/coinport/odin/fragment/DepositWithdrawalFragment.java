package com.coinport.odin.fragment;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.coinport.odin.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hoss on 14-11-23.
 */
public class DepositWithdrawalFragment extends Fragment {
    private String currency = "CNY";
    private FragmentManager fragmentManager;
    private RadioGroup subTabs;
    private DWFragmentCommon currentFragment = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.deposit_withdrawal_fragment, container, false);
        fragmentManager = getActivity().getFragmentManager();
        subTabs = (RadioGroup) view.findViewById(R.id.rg_tab);
        subTabs.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                android.app.Fragment fragment = FragmentFactory.getInstanceByIndex(checkedId, currency);
                transaction.replace(R.id.content, fragment);
                transaction.commit();
                currentFragment = (DWFragmentCommon) fragment;
            }
        });
        subTabs.check(R.id.radio_deposit);
        return view;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
        currentFragment.setCurrency(currency);
    }
    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
    }
}
