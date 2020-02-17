package com.android.artem.myapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignInActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    private FirebaseDatabase database;
    private DatabaseReference usersDatabaseReference;

    private Button loginSignUpButton;

    private static final String TAG = "SignInActivity";

    private TextInputLayout textInputEmail;
    private TextInputLayout textInputName;
    private TextInputLayout textInputPassword;
    private TextInputLayout textInputConfirmPassword;

    private TextView toggleLoginSignUpTextView;

    private boolean isLoginModeActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);




        database = FirebaseDatabase.getInstance();
        usersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");

        textInputEmail = findViewById(R.id.textInputEmail);
        textInputName = findViewById(R.id.textInputName);
        textInputPassword = findViewById(R.id.textInputPassword);
        textInputConfirmPassword = findViewById(R.id.textInputConfirmPassword);

        loginSignUpButton = findViewById(R.id.loginSignUpButton);
        toggleLoginSignUpTextView = findViewById(R.id.toggleLoginSignUpTextView);
    }

    private boolean validateEmail(){
        String emailInput = textInputEmail.getEditText().getText().toString().trim();
        if(emailInput.isEmpty()){
            textInputEmail.setError("Please input your email");
            return false;
        }else{
            textInputEmail.setError("");
            return true;
        }
    }

    private boolean validateName(){
        String nameInput = textInputName.getEditText().getText().toString().trim();
        if(nameInput.isEmpty()){
            textInputName.setError("Please input your name");
            return false;
        }else if(nameInput.length()>15){
            textInputName.setError("Name length have to be less than 15");
            return false;
        }else{
            textInputName.setError("");
            return true;
        }
    }

    private boolean validatePassword(){
        String passwordInput = textInputPassword.getEditText().getText().toString().trim();
        if(passwordInput.isEmpty()){
            textInputPassword.setError("Please input your password");
            return false;
        }else if(passwordInput.length()<7){
            textInputConfirmPassword.setError("Password length have to be more than 6");
            return false;
        }else{
            textInputConfirmPassword.setError("");
            return true;
        }
    }

    private boolean validateConfirmPassword(){
        String passwordInput = textInputPassword.getEditText().getText().toString().trim();
        String confirmPasswordInput = textInputConfirmPassword.getEditText().getText().toString().trim();
        if(!passwordInput.equals(confirmPasswordInput)){
            textInputConfirmPassword.setError("Passwords have to match");
            return false;
        }else{
            textInputConfirmPassword.setError("");
            return true;
        }
    }

    public void loginSignUpUser(View view) {

        if(!validateEmail() | !validateName() | !validatePassword()){
            return;
        }

        if(isLoginModeActive){
            auth.signInWithEmailAndPassword(textInputEmail.getEditText().getText().toString().trim(),
                    textInputPassword.getEditText().getText().toString().trim())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = auth.getCurrentUser();
                                startActivity(new Intent(SignInActivity.this, SearchListActivity.class));
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(SignInActivity.this, "Authentication failed.",
                                        Toast.LENGTH_LONG).show();

                            }


                        }
                    });
        }else{
            if(!validateEmail() | !validateName() | !validatePassword() | !validateConfirmPassword()){
                return;
            }
            auth.createUserWithEmailAndPassword(textInputEmail.getEditText().getText().toString().trim(),
                    textInputPassword.getEditText().getText().toString().trim())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = auth.getCurrentUser();
                                createUser(user);
                                startActivity(new Intent(SignInActivity.this, SearchListActivity.class));
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(SignInActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                }
                        }
                    });
        }



    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        //FirebaseUser currentUser = auth.getCurrentUser();
        //updateUI(currentUser);
    }

    private void createUser(FirebaseUser firebaseUser) {
        User user  = new User();
        user.setId(firebaseUser.getUid());
        user.setEmail(firebaseUser.getEmail());
        user.setName(textInputName.getEditText().getText().toString().trim());

        usersDatabaseReference.push().setValue(user);

    }


    public void toggleLoginSignUp(View view) {

        if(isLoginModeActive){
            isLoginModeActive = false;
            loginSignUpButton.setText("Sign Up");
            toggleLoginSignUpTextView.setText("Or, log in ");
            textInputConfirmPassword.setVisibility(View.VISIBLE);
        }else{

            isLoginModeActive = true;
            loginSignUpButton.setText("Log In");
            toggleLoginSignUpTextView.setText("Or, sign up");
            textInputConfirmPassword.setVisibility(View.GONE);
        }
    }
}
