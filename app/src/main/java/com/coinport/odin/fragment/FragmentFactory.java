package com.coinport.odin.fragment;

import android.app.Fragment;

import com.coinport.odin.R;

/**
 * Created by admin on 13-11-23.
 */
public class FragmentFactory {
    public static Fragment getInstanceByIndex(int index, String currency) {
        Fragment fragment = null;
        switch (index) {
            case R.id.radio_deposit:
                fragment = new DepositFragment(currency);
                break;
            case R.id.radio_withdrawal:
                fragment = new WithdrawalFragment(currency);
                break;
        }
        return fragment;
    }
}
