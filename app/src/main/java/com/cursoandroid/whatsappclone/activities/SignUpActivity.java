package com.cursoandroid.whatsappclone.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    private EditText nameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private User user;
    private Button signUpButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        nameEditText = findViewById(R.id.email_sign_in);
        emailEditText = findViewById(R.id.email_sign_up);
        passwordEditText = findViewById(R.id.password_sign_up);
        signUpButton = findViewById(R.id.sign_up);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                user = new User();
                user.setNome(nameEditText.getText().toString());
                user.setEmail(emailEditText.getText().toString());
                user.setPassword(passwordEditText.getText().toString());
                signUpUser();


            }
        });

    }

    private void signUpUser() {

        final FirebaseAuth auth = ConfiguracaoFirebase.getFirebaseAuth();
        auth.createUserWithEmailAndPassword(
                user.getEmail(),
                user.getPassword()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    Toast.makeText(SignUpActivity.this, "Sucesso ao cadastrar", Toast.LENGTH_SHORT).show();

                    String userId = Base64Custom.base64Code(user.getEmail());
                    user.setId(userId);
                    user.save();

                    Preferences preferences = new Preferences(SignUpActivity.this);
                    preferences.saveData(userId, user.getNome());
                    openUserSignIn();

                } else {

                    String errorException;

                    try {
                        throw Objects.requireNonNull(task.getException());

                    } catch (FirebaseAuthWeakPasswordException e) {
                        errorException = "Digite uma senha mais forte, com letras e números";

                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        errorException = "E-mail inválido";

                    } catch (FirebaseAuthUserCollisionException e) {
                        errorException = "E-mail já está em uso";

                    } catch (Exception e) {
                        errorException = "Erro";
                        e.printStackTrace();
                    }
                    Log.w("Warning", "createUserWithEmail:failure", task.getException());
                    Toast.makeText(SignUpActivity.this, "Erro: " + errorException, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void openUserSignIn() {
        startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
        finish();
    }
}
