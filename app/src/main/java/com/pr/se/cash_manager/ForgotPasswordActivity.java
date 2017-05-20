package com.pr.se.cash_manager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by ivanlazic on 11.04.17.
 */

public class ForgotPasswordActivity extends AppCompatActivity {
    private static final String TAG = "ForgotPasswordActivity";

    @InjectView(R.id.input_seqQuestionF) EditText _seqQuestionFText;
    @InjectView(R.id.link_login) TextView _loginLink;
    @InjectView(R.id.btn_forgot_password) Button _forgotPasswordButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Choose appropriate layout
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        double ySize = metrics.heightPixels / metrics.ydpi;
        double xSize = metrics.widthPixels / metrics.xdpi;
        double screenSize = Math.sqrt(xSize * xSize + ySize * ySize);

        if(screenSize < 4.5){
            setContentView(R.layout.activity_forgot_password_small);
        }else{
            setContentView(R.layout.activity_forgot_password_big);
        }

        ButterKnife.inject(this);

        _forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotPassword();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // No Changes - Return to the Login activity
                finish();
            }
        });
    }

    public void forgotPassword() {
        Log.d(TAG, "Forgot Password");

        if (!validate()) {
            onForgotPasswordFailed();
            return;
        }

        _forgotPasswordButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(ForgotPasswordActivity.this,
                R.style.AppTheme_NoActionBar);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Checking...");
        progressDialog.show();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSuccess or onFailed
                        // depending on success
                        onForgotPasswordSuccess();
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }

    public void onForgotPasswordSuccess() {
        _forgotPasswordButton.setEnabled(true);
        setResult(RESULT_OK, null);

        Intent intent = new Intent(getApplicationContext(), ChangePasswordActivity.class);
        startActivity(intent);
        finish();
    }

    public void onForgotPasswordFailed() {
        Toast.makeText(getBaseContext(), "Changing Password failed", Toast.LENGTH_LONG).show();
        _forgotPasswordButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;
        String seqQuestionF = _seqQuestionFText.getText().toString();

        File myDir = getFilesDir();
        StringBuilder totalSeqQuestion = new StringBuilder();

        try {
            File secondInputFile = new File(myDir, "seqQuestionFile");
            InputStream secondInputStream = new BufferedInputStream(new FileInputStream(secondInputFile));
            BufferedReader r = new BufferedReader(new InputStreamReader(secondInputStream));
            String line;
            while ((line = r.readLine()) != null) {
                totalSeqQuestion.append(line);
            }
            r.close();
            secondInputStream.close();
            Log.d("File", "File contents: " + totalSeqQuestion);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!seqQuestionF.equals(totalSeqQuestion.toString())) {
            _seqQuestionFText.setError("Answer does not match!");
            valid = false;
        } else {
            _seqQuestionFText.setError(null);
        }

        return valid;
    }
}