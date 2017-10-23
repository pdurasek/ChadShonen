package duoqbois.chadshonen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

public class LoginActivity extends AppCompatActivity
{

    private Toolbar mToolbar;

    private TextInputLayout mLoginEmail;
    private TextInputLayout mLoginPassword;

    private Button mLoginBtn;

    private ProgressDialog mLoginProgress;

    private FirebaseAuth mAuth;
    private String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        mToolbar = (Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Login");

        mLoginProgress = new ProgressDialog(this);

        mLoginEmail = (TextInputLayout) findViewById(R.id.login_email);
        mLoginPassword = (TextInputLayout) findViewById(R.id.login_password);
        mLoginBtn = (Button) findViewById(R.id.login_btn);

        mLoginBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String email = mLoginEmail.getEditText().getText().toString();
                String password = mLoginPassword.getEditText().getText().toString();

                if(!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password))
                {
                    mLoginProgress.show();
                    mLoginProgress.setTitle("Logging In");
                    mLoginProgress.setMessage("Checking your credentials.");
                    mLoginProgress.setCanceledOnTouchOutside(false);
                    mLoginProgress.show();

                    loginUser(email, password);
                }
                else
                {
                    Toast.makeText(LoginActivity.this, "Both email and password are required.",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void loginUser(String email, String password)
    {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            mLoginProgress.hide();

                            FirebaseAuthException e = (FirebaseAuthException) task.getException();
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(LoginActivity.this, "Error while trying to authenticate the user: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            mLoginProgress.dismiss();

                            Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(mainIntent);
                            finish();
                        }

                    }
                });
    }
}
