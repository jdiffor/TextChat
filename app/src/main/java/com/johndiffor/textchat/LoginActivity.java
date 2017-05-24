package com.johndiffor.textchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class LoginActivity extends AppCompatActivity {

    private static final String REQUIRED = "Required";
    private static final String NOT_AVAILABLE = "Not available";
    private static final String TAG = "EmailPassword";

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference usersRef = database.getReference("Users");

    boolean createdNewAccount = false;

    ArrayList<String> usedDisplayNames = new ArrayList<>();

    private EditText mEmailField;
    private EditText mPasswordField;
    private Button mLoginButton;
    private Button mCreateNewAccountButton;
    private Button mCreateAccountButton;
    private EditText mNewEmailField;
    private EditText mNewPasswordField;
    private EditText mNewDisplayNameField;
    private Button mGoBackButton;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Initialize UI objects
        mEmailField = (EditText) findViewById(R.id.email);
        mPasswordField = (EditText) findViewById(R.id.password);
        mLoginButton = (Button) findViewById(R.id.loginButton);
        mCreateNewAccountButton = (Button) findViewById(R.id.createNewAccountButton);
        mCreateAccountButton = (Button) findViewById(R.id.createAccountButton);
        mNewEmailField = (EditText) findViewById(R.id.newEmail);
        mNewPasswordField = (EditText) findViewById(R.id.newPassword);
        mNewDisplayNameField = (EditText) findViewById(R.id.newDisplayName);
        mGoBackButton = (Button) findViewById(R.id.goBackButton);

        //Add functionality for buttons
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
                createdNewAccount = false;
            }
        });

        mCreateNewAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccountUI();
            }
        });

        mCreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("EMAIL", mNewEmailField.getText().toString());
                Log.d("PASSWORD", mNewPasswordField.getText().toString());

                createAccount(mNewEmailField.getText().toString(), mNewPasswordField.getText().toString());
                createdNewAccount = true;
            }
        });

        mGoBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUI();
            }
        });


        mAuth = FirebaseAuth.getInstance();


        mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null) {
                    //User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    String userUID = user.getUid();

                    //If user created a new account, add them to the database
                    if(createdNewAccount) {
                        User newUser = new User(mNewDisplayNameField.getText().toString(), userUID);
                        usersRef.push().setValue(newUser);
                    }

                } else {
                    //User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                updateUI(user);
            }
        };

        //Get all the display names already being used
        usersRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);
                usedDisplayNames.add(user.getDisplayName().toLowerCase());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onBackPressed() {
        //do nothing
    }

    private void createAccount(String email, String password) {
        signOut();
        Log.d(TAG, "createAccount:" + email);
        if(!validateNewAccountForm()) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, R.string.create_account_failed, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(LoginActivity.this, R.string.creating_account, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if(!validateLoginForm()) {
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(LoginActivity.this, R.string.login_failed,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    //Show fields for creating a new account
    private void createAccountUI() {
        findViewById(R.id.createAccountPage).setVisibility(View.VISIBLE);
        findViewById(R.id.loginPage).setVisibility(View.GONE);
    }

    //Show fields for logging into an account
    private void loginUI() {
        findViewById(R.id.createAccountPage).setVisibility(View.GONE);
        findViewById(R.id.loginPage).setVisibility(View.VISIBLE);
    }

    private void signOut() {
        mAuth.signOut();
    }

    //If login is successful, switch to the user home activity
    private void updateUI(FirebaseUser user) {
        if(user != null) {
            Intent intent = new Intent(LoginActivity.this, UserHomeActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Ensure that the login forms aren't empty
     *
     * @return whether the form is valid
     */
    private boolean validateLoginForm() {
        boolean valid = true;
        String email = mEmailField.getText().toString();
        if(TextUtils.isEmpty(email)) {
            mEmailField.setError(REQUIRED);
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if(TextUtils.isEmpty(password)) {
            mPasswordField.setError(REQUIRED);
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    /**
     * Ensure that the forms aren't empty and that the display name is available
     *
     * @return whether form is valid
     */
    private boolean validateNewAccountForm() {
        boolean valid = true;

        String displayName = mNewDisplayNameField.getText().toString();
        if (TextUtils.isEmpty(displayName)) {
            mNewDisplayNameField.setError(REQUIRED);
            valid = false;
        } else if(usedDisplayNames.contains(displayName.toLowerCase())) {
            mNewDisplayNameField.setError(NOT_AVAILABLE);
            valid = false;

            Toast.makeText(LoginActivity.this, R.string.display_name_in_use, Toast.LENGTH_LONG).show();
        } else {
            mNewDisplayNameField.setError(null);
        }

        String email = mNewEmailField.getText().toString();
        if(TextUtils.isEmpty(email)) {
            mNewEmailField.setError(REQUIRED);
            valid = false;
        } else {
            mNewEmailField.setError(null);
        }

        String password = mNewPasswordField.getText().toString();
        if(TextUtils.isEmpty(password)) {
            mNewPasswordField.setError(REQUIRED);
            valid = false;
        } else {
            mNewPasswordField.setError(null);
        }

        return valid;
    }
}
