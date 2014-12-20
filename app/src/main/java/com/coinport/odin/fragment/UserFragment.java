package com.coinport.odin.fragment;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.coinport.odin.App;
import com.coinport.odin.R;
import com.coinport.odin.activity.AssetActivity;
import com.coinport.odin.activity.BindPhoneActivity;
import com.coinport.odin.activity.ChangePwActivity;
import com.coinport.odin.activity.LoginActivity;
import com.coinport.odin.activity.ResetPwActivity;
import com.coinport.odin.activity.UserVerifyActivity;
import com.coinport.odin.obj.AccountInfo;

public class UserFragment extends Fragment {
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.user_fragment, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        initUi();
    }

    private void initUi() {
        if (App.getAccount().uid == null) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            return;
        }
        AccountInfo info = App.getAccount();
        String helloTo;
        if (info.realname != null) {
            helloTo = info.realname;
        } else if (info.nickname != null) {
            helloTo = info.nickname;
        } else {
            helloTo = info.username;
        }
        TextView userName = (TextView) view.findViewById(R.id.user_name);
        userName.setText(helloTo);
        TextView accountNumber = (TextView) view.findViewById(R.id.account_number);
        accountNumber.setText(info.uid);
        TextView accountName = (TextView) view.findViewById(R.id.account_name);
        accountName.setText(info.username);
        TextView accountPhone = (TextView) view.findViewById(R.id.account_phone);
        if (info.mobileVerified) {
            accountPhone.setText(info.mobile);
        } else {
            accountPhone.setText(R.string.phone_unverified);
        }
        TableRow userVerify = (TableRow) view.findViewById(R.id.user_verify);
        if (info.realname != null) {
            userVerify.setClickable(false);
            ImageView usvi = (ImageView) view.findViewById(R.id.user_secure_verify_icon);
            usvi.setVisibility(View.GONE);
            TextView usv = (TextView) view.findViewById(R.id.user_secure_verified);
            usv.setVisibility(View.VISIBLE);
        } else {
            userVerify.setClickable(true);
            userVerify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), UserVerifyActivity.class);
                    getActivity().startActivity(intent);
                }
            });
        }
        TableRow bindPhone = (TableRow) view.findViewById(R.id.bind_phone);
        if (info.mobileVerified) {
            bindPhone.setClickable(false);
            ImageView usbpi = (ImageView) view.findViewById(R.id.user_secure_bind_phone_icon);
            usbpi.setVisibility(View.GONE);
            TextView uspb = (TextView) view.findViewById(R.id.user_secure_phone_bind);
            uspb.setVisibility(View.VISIBLE);
        } else {
            bindPhone.setClickable(true);
            bindPhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), BindPhoneActivity.class);
                    getActivity().startActivity(intent);
                }
            });
        }
        LinearLayout userAsset = (LinearLayout) view.findViewById(R.id.user_asset);
        userAsset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), AssetActivity.class);
                getActivity().startActivity(intent);
            }
        });
        TableRow changePw = (TableRow) view.findViewById(R.id.change_pw);
        changePw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), ChangePwActivity.class);
                getActivity().startActivity(intent);
            }
        });
    }
}
