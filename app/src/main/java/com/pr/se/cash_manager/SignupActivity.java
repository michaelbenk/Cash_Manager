package com.pr.se.cash_manager;

import android.content.Context;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SignupActivity extends AppCompatActivity {
    private final String TAG = getString(R.string.activity_title_Signup);

    @InjectView(R.id.input_seqQuestion) EditText seqQuestionText;
    @InjectView(R.id.input_username) EditText usernameText;
    @InjectView(R.id.input_password) EditText passwordText;
    @InjectView(R.id.btn_signup) Button signupButton;
    @InjectView(R.id.link_login) TextView loginLink;

    // METHOD to create a new File with fileName and the content
    public void createFile(String fileName, String content) {
        FileOutputStream fos = null;

        try {
            fos = openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(content.getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Choose appropriate layout
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        double ySize = metrics.heightPixels / metrics.ydpi;
        double xSize = metrics.widthPixels / metrics.xdpi;
        double screenSize = Math.sqrt(xSize * xSize + ySize * ySize);

        if(screenSize < 4.5){
            setContentView(R.layout.activity_signup_small);
        }else{
            setContentView(R.layout.activity_signup_big);
        }

        ButterKnife.inject(this);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    File dir = getFilesDir();

                    deleteRecursive(dir.getPath());

                    signup();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    public void signup() throws IOException {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_NoActionBar);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.signup_creatingAccount));
        progressDialog.show();

        String seqQuestion = seqQuestionText.getText().toString();
        String username = usernameText.getText().toString();
        String password = passwordText.getText().toString();

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

    public void onSignupSuccess() {
        signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), R.string.error_signup_loginFaild, Toast.LENGTH_LONG).show();
        signupButton.setEnabled(true);
    }

    public boolean validate() throws IOException {
        boolean valid = true;

        String seqQuestion = seqQuestionText.getText().toString();
        String username = usernameText.getText().toString();
        String password = passwordText.getText().toString();

        if (username.isEmpty() || username.length() < 3) {
            usernameText.setError(getString(R.string.error_signup_3_num_characters));
            valid = false;
        } else {
            usernameText.setError(null);
            createFile("usernameFile", username);
        }

        if (password.isEmpty() || password.length() < 6) {
            passwordText.setError(getString(R.string.error_signup_7_num_characters));
            valid = false;
        } else {
            createFile("passwordFile", password);
            passwordText.setError(null);
        }

        if (seqQuestion.isEmpty() || seqQuestion.length() < 3) {
            seqQuestionText.setError(getString(R.string.error_signup_3_num_characters));
            valid = false;
        } else {
            createFile("seqQuestionFile", seqQuestion);
            seqQuestionText.setError(null);
        }

        return valid;
    }

    private void deleteRecursive(String strPath) {

        File fileOrDirectory = new File(strPath);

        if (fileOrDirectory.isDirectory()){
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child.getPath());
            fileOrDirectory.delete();
        }else{

            fileOrDirectory.delete();
        }
    }
}
