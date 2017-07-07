package com.pr.se.cash_manager;

import android.content.Context;
import android.content.SharedPreferences;
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

import butterknife.ButterKnife;
import butterknife.InjectView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by ivanlazic on 11.04.17.
 * <p>
 * The class SignupActivity allows users to signup / register / create an account with an username,
 * password and security question in order to use the applications functionalities.
 * </p>
 */
public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    @InjectView(R.id.input_seqQuestion)
    EditText _seqQuestionText;
    @InjectView(R.id.input_username)
    EditText _usernameText;
    @InjectView(R.id.input_password)
    EditText _passwordText;
    @InjectView(R.id.btn_signup)
    Button _signupButton;
    @InjectView(R.id.link_login)
    TextView _loginLink;
    private SharedPreferences prefs;

    // METHOD to create a new File with fileName and content
    private void createFile(String fileName, String content) {
        FileOutputStream fos = null;

        try {
            fos = openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(content.getBytes());
            fos.close();
        } catch (Exception e) {
            Log.e(TAG, "Couldn't create file", e);
        }
    }

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
            setContentView(R.layout.activity_signup_small);
        } else {
            setContentView(R.layout.activity_signup_big);
        }

        ButterKnife.inject(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            public SharedPreferences prefs;

            // METHOD to start the signup-process when the user clicks on signup-button
            @Override
            public void onClick(View v) {
                try {
                    signup();
                    this.prefs = getSharedPreferences("com.pr.se.cash_manager", MODE_PRIVATE);
                    this.prefs.edit().putBoolean("firstFilter", true).apply();
                    deleteLists();
                } catch (IOException e) {
                    Log.e(TAG, "Signup process failed", e);
                }
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    // METHOD to perform the signup and validate the users input
    private void signup() throws IOException {
        Log.d(TAG, "Signup");

        this.prefs = getSharedPreferences("com.pr.se.cash_manager", MODE_PRIVATE);
        this.prefs.edit().putBoolean("firstFilter", true).apply();
        this.prefs.edit().putBoolean("firstrun", true).apply();

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_NoActionBar);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String seqQuestion = _seqQuestionText.getText().toString();
        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignupSuccess();
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }

    // METHOD to succesfully end the signup process and finish the activity
    private void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    // METHOD to inform the user about the failed signup, due to invalid input
    private void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Signup failed", Toast.LENGTH_LONG).show();
        _signupButton.setEnabled(true);
    }

    // METHOD to check whether the user input fulfills the given criteria
    private boolean validate() throws IOException {
        boolean valid = true;

        String seqQuestion = _seqQuestionText.getText().toString();
        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();

        if (username.isEmpty() || username.length() < 3) {
            _usernameText.setError("at least 3 alphanumeric characters!");
            valid = false;
        } else {
            _usernameText.setError(null);
            createFile("usernameFile", username);
        }

        if (password.isEmpty() || password.length() < 6) {
            _passwordText.setError("at least 7 alphanumeric characters!");
            valid = false;
        } else {
            createFile("passwordFile", password);
            _passwordText.setError(null);
        }

        if (seqQuestion.isEmpty() || seqQuestion.length() < 3) {
            _seqQuestionText.setError("at least 3 alphanumeric characters!");
            valid = false;
        } else {
            createFile("seqQuestionFile", seqQuestion);
            _seqQuestionText.setError(null);
        }

        return valid;
    }

    // METHOD to delete past user information after a signup
    private void deleteLists() {
        RW.writeCategories(this, new ArrayList<Category>(), "categories");
        RW.writeExpenses(this, new ArrayList<Expense>(), "expenses");
        RW.writeFilter(this, new ArrayList<Filter>(), "filters");
    }
}