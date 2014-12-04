package com.coinport.odin.fragment;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

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
    private DepositFragment depositFragment;
    private WithdrawalFragment withdrawalFragment;
    private Map<Integer, android.app.Fragment> fragmentMap = new HashMap<Integer, android.app.Fragment>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.deposit_withdrawal_fragment, container, false);
        depositFragment = (DepositFragment) FragmentFactory.getInstanceByIndex(R.id.radio_deposit, currency);
        withdrawalFragment = (WithdrawalFragment) FragmentFactory.getInstanceByIndex(R.id.radio_withdrawal, currency);
        fragmentMap.put(R.id.radio_deposit, depositFragment);
        fragmentMap.put(R.id.radio_withdrawal, withdrawalFragment);
        fragmentManager = getActivity().getFragmentManager();
        subTabs = (RadioGroup) view.findViewById(R.id.rg_tab);
        subTabs.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                android.app.Fragment fragment = fragmentMap.get(checkedId);
                transaction.replace(R.id.content, fragment);
                transaction.commit();
            }
        });
        subTabs.check(R.id.radio_deposit);
        return view;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
        depositFragment.setCurrency(currency);
        withdrawalFragment.setCurrency(currency);
//        TextView tv = (TextView) getActivity().findViewById(R.id.test_tv);
//        tv.setText(this.currency);
    }
    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
    }
}
