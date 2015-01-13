package com.coinport.odin.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.coinport.odin.App;
import com.coinport.odin.R;
import com.coinport.odin.network.NetworkAsyncTask;
import com.coinport.odin.network.NetworkRequest;
import com.coinport.odin.network.OnApiResponseListener;
import com.coinport.odin.util.Constants;
import com.coinport.odin.util.Util;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class AddBankCardFragment extends DialogFragment implements DialogInterface.OnClickListener,
        View.OnClickListener {
    private OnClickListener positiveListener = null;
    private OnClickListener negativeListener = null;
    private View view;
    private static String addBankEmailUUID = "";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        view = inflater.inflate(R.layout.add_bank_card_fragment, null);
        Spinner bank = (Spinner) view.findViewById(R.id.withdrawal_abc_bank);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.bank_array,
                R.layout.black_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bank.setAdapter(spinnerAdapter);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity()).setTitle(
            R.string.withdrawal_abc_title).setView(view);
        if (positiveListener != null)
            dialogBuilder.setPositiveButton(R.string.withdrawal_abc_ok, this);
        if (negativeListener != null)
            dialogBuilder.setNegativeButton(R.string.withdrawal_abc_cancel, this);
        TextView realname = (TextView) view.findViewById(R.id.withdrawal_abc_name);
        realname.setText(App.getAccount().realname);
        realname.setEnabled(false);
        Button getEmailCodeBtn = (Button) view.findViewById(R.id.get_email_code);
        getEmailCodeBtn.setOnClickListener(this);
        Dialog dialog = dialogBuilder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
		// change dialog width
		if (getDialog() != null) {

			int fullWidth;

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
				Display display = getActivity().getWindowManager().getDefaultDisplay();
				Point size = new Point();
				display.getSize(size);
				fullWidth = size.x;
			} else {
				Display display = getActivity().getWindowManager().getDefaultDisplay();
				fullWidth = display.getWidth();
			}

			final int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
					.getDisplayMetrics());

			int w = fullWidth - padding;
			int h = getDialog().getWindow().getAttributes().height;

			getDialog().getWindow().setLayout(w, h);
		}
    }

    public AddBankCardFragment setPositiveButton(OnClickListener listener) {
        this.positiveListener = listener;
        return this;
    }

    public AddBankCardFragment setNegativeButton(OnClickListener listener) {
        this.negativeListener = listener;
        return this;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == -1) {
            final String nameStr = ((TextView) view.findViewById(R.id.withdrawal_abc_name)).getText().toString();
            final String bankStr = ((Spinner) view.findViewById(R.id.withdrawal_abc_bank)).getSelectedItem().toString();
            final String accountStr = ((TextView) view.findViewById(R.id.withdrawal_abc_account)).getText().toString();
            final String branchStr = ((TextView) view.findViewById(R.id.withdrawal_abc_branch_bank)).getText().toString();
            String codeStr = ((TextView) view.findViewById(R.id.withdrawal_abc_email_code)).getText().toString();
            if (nameStr.equals("") || accountStr.equals("") || bankStr.equals("") || codeStr.equals("")) {
                Toast.makeText(getActivity(), getString(R.string.add_bc_info_lack), Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, String> params = new HashMap<>();
            params.put("bankName", bankStr);
            params.put("ownerName", nameStr);
            params.put("cardNumber", accountStr);
            params.put("branchBankName", branchStr);
            params.put("emailCode", codeStr);
            params.put("verifyCodeUuidEmail", addBankEmailUUID);

            NetworkAsyncTask task = new NetworkAsyncTask(Constants.ADD_BANK_CARD_URL, Constants.HttpMethod.POST)
                    .setOnSucceedListener(new OnApiResponseListener())
                    .setOnFailedListener(new OnApiResponseListener())
                    .setRenderListener(new NetworkAsyncTask.OnPostRenderListener() {
                        @Override
                        public void onRender(NetworkRequest s) {
                            if (s.getApiStatus() == NetworkRequest.ApiStatus.SUCCEED) {
                                Bundle args = new Bundle();
                                args.putString("bankName", bankStr);
                                args.putString("ownerName", nameStr);
                                args.putString("cardNumber", accountStr);
                                args.putString("branchBankName", branchStr);
                                positiveListener.onClick(args);
                            }
                        }
                    });
            task.execute(params);


//            final Bundle fargs = args;
//            new AlertDialog.Builder(getActivity()).setMessage("BUTTON_POSITIVE")
//                    .setTitle("Alert Postive ")
//                    .setNeutralButton(android.R.string.ok,
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int whichButton){
//                                    positiveListener.onClick(fargs);
//                                }
//                            })
//                    .show();
        } else {
            negativeListener.onClick(new Bundle());
        }
    }

    @Override
    public void onClick(View v) {
        Util.countdownButton((Button) v);
        NetworkAsyncTask task = new NetworkAsyncTask(Constants.EMAIL_CODE_URL, Constants.HttpMethod.GET)
                .setOnSucceedListener(new OnApiResponseListener())
                .setOnFailedListener(new OnApiResponseListener())
                .setRenderListener(new NetworkAsyncTask.OnPostRenderListener() {
                    @Override
                    public void onRender(NetworkRequest s) {
                        if (!isAdded())
                            return;
                        if (s.getApiStatus() != NetworkRequest.ApiStatus.SUCCEED) {
                            if (s.getApiStatus() == NetworkRequest.ApiStatus.UNAUTH) {
                                Intent intent = Util.toLoginFromAuthFail(AddBankCardFragment.this.getActivity());
                                AddBankCardFragment.this.getActivity().startActivity(intent);
                            } else {
                                Toast.makeText(getActivity(), getString(R.string.request_failed),
                                        Toast.LENGTH_SHORT).show();
                            }
                            return;
                        }
                        try {
                            addBankEmailUUID= s.getApiResult().getString("data");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        task.execute();
    }

    interface OnClickListener {
        public void onClick(Bundle args);
    }
}
