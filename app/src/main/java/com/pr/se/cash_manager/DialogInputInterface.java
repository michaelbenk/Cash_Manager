package com.pr.se.cash_manager;


import android.view.View;

public interface DialogInputInterface {
    // onBuildDialog() is called when the dialog builder is ready to accept a view to insert
    // into the dialog
    View onBuildDialog();

    // onCancel() is called when the user clicks on 'Cancel'
    void onCancel();

    // onResult(View v) is called when the user clicks on 'Ok'
    void onResult(View v);
}
