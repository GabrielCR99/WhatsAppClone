package com.cursoandroid.whatsappclone.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cursoandroid.whatsappclone.R;
import com.cursoandroid.whatsappclone.config.ConfiguracaoFirebase;
import com.cursoandroid.whatsappclone.helper.Base64Custom;
import com.cursoandroid.whatsappclone.helper.Preferences;
import com.cursoandroid.whatsappclone.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class SignInActivity extends AppCompatActivity {

    private EditText nameEditText;
    private EditText passwordEditText;
    private User user;
    private FirebaseAuth firebaseAuth;
    private ValueEventListener valueEventListener;
    private DatabaseReference reference;
    private String signedInUserIdentifier;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        checkIfUserIsSignedIn();

        nameEditText = findViewById(R.id.email_sign_in);
        passwordEditText = findViewById(R.id.pasword_sign_in);
        Button signInButton = findViewById(R.id.login);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = new User();
                user.setEmail(nameEditText.getText().toString());
                user.setPassword(passwordEditText.getText().toString());
                validateLogin();

            }
        });
    }

    private void checkIfUserIsSignedIn() {
        firebaseAuth = ConfiguracaoFirebase.getFirebaseAuth();
        if (firebaseAuth.getCurrentUser() != null) {
            openMainActivity();
        } else {

        }
    }

    private void validateLogin() {

        firebaseAuth = ConfiguracaoFirebase.getFirebaseAuth();
        firebaseAuth.signInWithEmailAndPassword(
                user.getEmail(),
                user.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    signedInUserIdentifier = Base64Custom.base64Code(user.getEmail());

                    reference = ConfiguracaoFirebase.getFirebase()
                            .child("Users")
                            .child(signedInUserIdentifier);

                    valueEventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User retrievedUser = dataSnapshot.getValue(User.class);

                            Preferences preferences = new Preferences(SignInActivity.this);
                            preferences.saveData(signedInUserIdentifier, Objects.requireNonNull(retrievedUser).getNome());


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    };
                    reference.addListenerForSingleValueEvent(valueEventListener);

                    openMainActivity();
                    Toast.makeText(SignInActivity.this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();

                } else {

                    String errorException;
                    try {

                        throw Objects.requireNonNull(task.getException());

                    } catch (FirebaseAuthInvalidUserException e) {
                        errorException = "Usuario invalido";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        errorException = "Credenciais invalidas";
                    } catch (Exception e) {
                        errorException = "Erro";
                        e.printStackTrace();

                    }
                    Toast.makeText(SignInActivity.this, "Erro: " + errorException, Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    private void openMainActivity() {
        startActivity(new Intent(SignInActivity.this, MainActivity.class));
        finish();
    }

    public void goToSignUp(View view) {
        startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
    }
}
