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

/**
 * Created by ivanlazic on 11.04.17.
 * <p>
 * The class ForgotPasswordActivity allows users to change their password by entering a new password
 * </p>
 */
public class ChangePasswordActivity extends AppCompatActivity {

    private static final String TAG = "ChangePasswordActivity";
    private static final int REQUEST_SIGNUP = 0;

    @InjectView(R.id.input_password) EditText passwordText;
    @InjectView(R.id.input_password_conf) EditText passwordConfText;
    @InjectView(R.id.btn_change_password) Button changePasswordButton;
    @InjectView(R.id.link_login) TextView loginLink;

    // METHOD to initialize the class with the appropriate layout
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

    // METHOD to perform the password change and validate the users input
    private void changePassword() {
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

    // METHOD check whether the request-code is suitable and finish the activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    // METHOD to disable going back to the MainActivity
    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    // METHOD to succesfully end the password-change process and finish the activity
    private void onChangePasswordSuccess() {
        changePasswordButton.setEnabled(true);
        finish();
    }

    // METHOD to inform the user about the failed password-change, due to invalid input
    private void onChangePasswordFailed() {
        Toast.makeText(getBaseContext(), "Passwords do not match!", Toast.LENGTH_LONG).show();
        changePasswordButton.setEnabled(true);
    }

    // METHOD to check whether the user input fulfills the given criteria
    private boolean validate() {
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

    // METHOD to create a new file with the new password by replacing the old file
    private void createFile(String fileName, String content) {
        FileOutputStream fos;

        try {
            fos = openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(content.getBytes());
            fos.close();
        } catch (Exception e) {
            Log.e(TAG, "Couldn't create file with new password", e);
        }
    }
}
