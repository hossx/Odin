package com.coinport.odin.dialog;

import android.app.AlertDialog;
import android.content.Context;

public class BankCardSelectDialog extends AlertDialog {
    protected BankCardSelectDialog(Context context) {
        super(context);
    }

    public BankCardSelectDialog(Context context, int theme) {
        super(context, theme);
    }

    protected BankCardSelectDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
}
