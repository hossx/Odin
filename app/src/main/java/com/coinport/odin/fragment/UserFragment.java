package com.coinport.odin.fragment;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.coinport.odin.R;
import com.coinport.odin.activity.AssetActivity;
import com.coinport.odin.activity.BindPhoneActivity;
import com.coinport.odin.activity.ChangePwActivity;
import com.coinport.odin.activity.ResetPwActivity;
import com.coinport.odin.activity.UserVerifyActivity;

public class UserFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_fragment, container, false);
        TextView userName = (TextView) view.findViewById(R.id.user_name);
        userName.setText("m_chao_1984@163.com");
        LinearLayout userAsset = (LinearLayout) view.findViewById(R.id.user_asset);
        userAsset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), AssetActivity.class);
                getActivity().startActivity(intent);
            }
        });
        TableRow userVerify = (TableRow) view.findViewById(R.id.user_verify);
        userVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), UserVerifyActivity.class);
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
        TableRow bindPhone = (TableRow) view.findViewById(R.id.bind_phone);
        bindPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), BindPhoneActivity.class);
                getActivity().startActivity(intent);
            }
        });
        return view;
    }
}
