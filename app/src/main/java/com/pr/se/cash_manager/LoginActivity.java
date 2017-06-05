package com.pr.se.cash_manager;

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

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    @InjectView(R.id.input_username) EditText usernameText;
    @InjectView(R.id.input_password) EditText passwordText;
    @InjectView(R.id.btn_login) Button loginButton;
    @InjectView(R.id.link_signup) TextView signupLink;
    @InjectView(R.id.link_forgot_password) TextView forgotPasswordLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Choose appropriate layout
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        double ySize = metrics.heightPixels / metrics.ydpi;
        double xSize = metrics.widthPixels / metrics.xdpi;
        double screenSize = Math.sqrt(xSize * xSize + ySize * ySize);

        if(screenSize < 4.5){
            setContentView(R.layout.activity_login_small);
        }else{
            setContentView(R.layout.activity_login_big);
        }

        ButterKnife.inject(this);

        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });

        forgotPasswordLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_NoActionBar);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String username = usernameText.getText().toString();
        String password = passwordText.getText().toString();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();
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

    public void onLoginSuccess() {
        loginButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Invalid username or password!", Toast.LENGTH_LONG).show();
        loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String username = usernameText.getText().toString();
        String password = passwordText.getText().toString();
        File myDir = getFilesDir();
        StringBuilder totalUsername = new StringBuilder();
        StringBuilder totalPassword = new StringBuilder();

        try {
            File secondInputFile = new File(myDir, "usernameFile");
            InputStream secondInputStream = new BufferedInputStream(new FileInputStream(secondInputFile));
            BufferedReader r = new BufferedReader(new InputStreamReader(secondInputStream));
            String line;
            while ((line = r.readLine()) != null) {
                totalUsername.append(line);
            }
            r.close();
            secondInputStream.close();
            Log.d("File", "File contents: " + totalUsername);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            File secondInputFile = new File(myDir, "passwordFile");
            InputStream secondInputStream = new BufferedInputStream(new FileInputStream(secondInputFile));
            BufferedReader r = new BufferedReader(new InputStreamReader(secondInputStream));
            String line;
            while ((line = r.readLine()) != null) {
                totalPassword.append(line);
            }
            r.close();
            secondInputStream.close();
            Log.d("File", "File contents: " + totalPassword);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if ( (totalUsername.toString().equals(username)) && (totalPassword.toString().equals(password)) ) {
            usernameText.setError(null);
            passwordText.setError(null);
        } else {
            valid = false;
        }

        return valid;
    }
}