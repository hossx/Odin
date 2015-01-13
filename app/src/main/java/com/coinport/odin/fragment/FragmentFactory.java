package com.coinport.odin.fragment;

import android.support.v4.app.Fragment;

import com.coinport.odin.R;

public class FragmentFactory {
    public static Fragment getInstanceByIndex(int index, String currency) {
        Fragment fragment = null;
        switch (index) {
            case R.id.radio_deposit:
                fragment = DepositFragment.newInstance(currency);
                break;
            case R.id.radio_withdrawal:
                fragment = WithdrawalFragment.newInstance(currency);
                break;
        }
        return fragment;
    }
}
