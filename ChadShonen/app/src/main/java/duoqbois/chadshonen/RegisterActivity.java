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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity
{

    // UI elements
    private TextInputLayout mDisplayName;
    private TextInputLayout mEmail;
    private TextInputLayout mPassword;
    private Button mCreateBtn;

    // Toolbar
    private Toolbar mToolbar;

    // ProgressDialog
    private ProgressDialog mRegProgress;

    // Firebase Auth
    private FirebaseAuth mAuth;
    private String TAG = "REGISTER";
    DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Set Toolbar
        mToolbar = (Toolbar) findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create Account"); // TODO possibly handle exceptions
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        mRegProgress = new ProgressDialog(this);

        // Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Register fields
        mDisplayName = (TextInputLayout) findViewById(R.id.reg_display_name);
        mEmail = (TextInputLayout) findViewById(R.id.reg_email);
        mPassword = (TextInputLayout) findViewById(R.id.reg_password);
        mCreateBtn = (Button) findViewById(R.id.reg_create_button);

        mCreateBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // TODO possibly handle exceptions
                String display_name = mDisplayName.getEditText().getText().toString();
                String email = mEmail.getEditText().getText().toString();
                String password = mPassword.getEditText().getText().toString();

                if (TextUtils.isEmpty(display_name) || display_name.length() < 6)
                {
                    Toast.makeText(RegisterActivity.this, "Display name must be at least 6 characters long",
                            Toast.LENGTH_LONG).show();
                }
                else if (TextUtils.isEmpty(email))
                {
                    Toast.makeText(RegisterActivity.this, "Email must be be specified",
                            Toast.LENGTH_LONG).show();
                }
                else if (TextUtils.isEmpty(password) || password.length() < 6)
                {
                    Toast.makeText(RegisterActivity.this, "Password must be at least 6 characters long",
                            Toast.LENGTH_LONG).show();
                }
                else
                {
                    mRegProgress.setTitle("Registering User");
                    mRegProgress.setMessage("Creating a new account!");
                    mRegProgress.setCanceledOnTouchOutside(false);
                    mRegProgress.show();

                    registerUser(display_name, email, password);
                }
            }
        });
    }

    /**
     * Try registering a new user with a display name, email and a password.
     *
     * @param display_name user provided display name
     * @param email        user email
     * @param password     user password
     */
    private void registerUser(final String display_name, String email, String password)
    {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful())
                        {
                            mRegProgress.hide();

                            // TODO possibly handle exceptions
                            FirebaseAuthException e = (FirebaseAuthException) task.getException();
                            Toast.makeText(RegisterActivity.this, "Error while trying to register: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            String uid = "";
                            if (currentUser != null)
                            {
                                uid = currentUser.getUid();
                            }

                            // Store user registration data
                            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                            HashMap<String, String> userMap = new HashMap<>();
                            userMap.put("name", display_name);
                            userMap.put("status", "Hi there, I'm using ChadShonen");
                            userMap.put("image", "default_image.jpg");
                            userMap.put("thumb_image", "default");

                            myRef.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        mRegProgress.dismiss();

                                        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(mainIntent);
                                        finish();
                                    }
                                }
                            });

                        }
                    }
                });
    }
}
