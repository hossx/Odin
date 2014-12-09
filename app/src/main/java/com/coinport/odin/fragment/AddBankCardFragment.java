package com.coinport.odin.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.coinport.odin.R;

public class AddBankCardFragment extends DialogFragment {
    private OnClickListener positiveListener = null;
    private OnClickListener negativeListener = null;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View v = inflater.inflate(R.layout.add_bank_card_fragment, null);
        Spinner bank = (Spinner) v.findViewById(R.id.withdrawal_abc_bank);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.bank_array,
                R.layout.black_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bank.setAdapter(spinnerAdapter);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity()).setTitle(
            R.string.withdrawal_abc_title).setView(v);
        if (positiveListener != null)
            dialogBuilder.setPositiveButton(R.string.withdrawal_abc_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    TextView name = (TextView) v.findViewById(R.id.withdrawal_abc_name);
                    Bundle args = new Bundle();
                    args.putString(getString(R.string.withdrawal_abc_name), name.getText().toString());

                    final Bundle fargs = args;
                    new AlertDialog.Builder(getActivity()).setMessage("BUTTON_POSITIVE")
                            .setTitle("Alert Postive ")
                            .setCancelable(true)
                            .setNeutralButton(android.R.string.ok,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton){
                                            positiveListener.onClick(fargs);
                                        }
                                    })
                            .show();
                }
            });
        if (negativeListener != null)
            dialogBuilder.setNegativeButton(R.string.withdrawal_abc_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    negativeListener.onClick(new Bundle());
                }
            });
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

    interface OnClickListener {
        public void onClick(Bundle args);
    }
}
