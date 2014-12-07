package com.coinport.odin.fragment;

import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.coinport.odin.R;

public class AddBankCardFragment extends DialogFragment {

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		if (getDialog() != null) {
//			getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
            getDialog().setTitle(R.string.withdrawal_abc_title);
//			getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		}

//        setStyle(android.R.style.Animation_Dialog, R.style.DialogTheme);
		View root = inflater.inflate(R.layout.add_bank_card_fragment, container, false);

		return root;
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

}
