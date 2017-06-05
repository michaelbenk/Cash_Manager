package com.pr.se.cash_manager;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class ChangePasswordActivity extends AppCompatActivity {

    private static final String TAG = "ChangePasswordActivity";
    private static final int REQUEST_SIGNUP = 0;

    @InjectView(R.id.input_password) EditText passwordText;
    @InjectView(R.id.input_password_conf) EditText passwordConfText;
    @InjectView(R.id.btn_change_password) Button changePasswordButton;
    @InjectView(R.id.link_login) TextView loginLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Choose appropriate layout
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        double ySize = metrics.heightPixels / metrics.ydpi;
        double xSize = metrics.widthPixels / metrics.xdpi;
        double screenSize = Math.sqrt(xSize * xSize + ySize * ySize);

        if (screenSize < 4.5) {
            setContentView(R.layout.activity_change_password_small);
        } else {
            setContentView(R.layout.activity_change_password_big);
        }

        ButterKnife.inject(this);

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // No Changes - Return to the Login activity
                finish();
            }
        });

        changePasswordButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                changePassword();
            }
        });
    }

    public void changePassword() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onChangePasswordFailed();
            return;
        }

        changePasswordButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(ChangePasswordActivity.this,
                R.style.AppTheme_NoActionBar);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String password = passwordText.getText().toString();
        String passwordConf = passwordConfText.getText().toString();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onChangePasswordSuccess();
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onChangePasswordSuccess() {
        changePasswordButton.setEnabled(true);
        finish();
    }

    public void onChangePasswordFailed() {
        Toast.makeText(getBaseContext(), "Passwords do not match!", Toast.LENGTH_LONG).show();
        changePasswordButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String password = passwordText.getText().toString();
        String passwordConf = passwordConfText.getText().toString();


        if ( (password.equals(passwordConf)) && (password.length() >= 6) ) {
            createFile("passwordFile", password);
            passwordText.setError(null);
            passwordConfText.setError(null);
        } else {
            valid = false;
        }

        return valid;
    }

    public void createFile(String fileName, String content) {
        FileOutputStream fos;

        try {
            fos = openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(content.getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
