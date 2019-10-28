package com.cursoandroid.whatsappclone.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.cursoandroid.whatsappclone.R;
import com.cursoandroid.whatsappclone.adapter.TabAdapter;
import com.cursoandroid.whatsappclone.config.ConfiguracaoFirebase;
import com.cursoandroid.whatsappclone.helper.Base64Custom;
import com.cursoandroid.whatsappclone.helper.Preferences;
import com.cursoandroid.whatsappclone.helper.SlidingTabLayout;
import com.cursoandroid.whatsappclone.model.Contact;
import com.cursoandroid.whatsappclone.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    //objeto que fara o bd

    private FirebaseAuth firebaseAuth;
    private Toolbar toolbar;

    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;
    private String contactId;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = ConfiguracaoFirebase.getFirebaseAuth();

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("WhatsApp");
        setSupportActionBar(toolbar);

        slidingTabLayout = findViewById(R.id.sliding_tab);
        viewPager = findViewById(R.id.view_pager);

        //configurar slidingTabLayout
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(this, R.color.textColorPrimary));


        //configurar adapter
        TabAdapter tabAdapter = new TabAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabAdapter);

        slidingTabLayout.setViewPager(viewPager);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case R.id.leave:
                signOutUser();
                return true;

            case R.id.settings:
                return true;

            case R.id.add_person:
                openSignUpContact();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }

    private void openSignUpContact() {
        final EditText editText = new EditText(MainActivity.this);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setTitle(getString(R.string.new_contact))
                .setMessage("E-mail do usuário")
                .setCancelable(false)
                .setPositiveButton(getString(R.string.sign_up), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String emailContact = editText.getText().toString();

                        //validar se o email foi digitado

                        if (emailContact.isEmpty()) {
                            Toast.makeText(MainActivity.this, "Preencha com um email", Toast.LENGTH_SHORT).show();
                        } else {
                            //verifica se o usuario está cadastrado
                            contactId = Base64Custom.base64Code(emailContact);

                            //recupera instancia do firebase
                            databaseReference = ConfiguracaoFirebase.getFirebase().child("Users").child(contactId);

                            //consulta 1 vez, recupera os dados e nao notifica os dados caso sejam alterados.
                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.getValue() != null) {

                                        //recupera dados do contato a ser adicionado
                                        User contactUser = dataSnapshot.getValue(User.class);

                                        //recuperar identificador usuario logado Base64
                                        Preferences preferences = new Preferences(MainActivity.this);
                                        String signedInUserIdentifier = preferences.getIdentifier();

                                        databaseReference = ConfiguracaoFirebase.getFirebase();
                                        databaseReference = databaseReference.child("Contatos")
                                                .child(signedInUserIdentifier)
                                        .child(contactId);

                                        Contact contact = new Contact();
                                        contact.setUserIdentifier(contactId);
                                        contact.setEmail(Objects.requireNonNull(contactUser).getEmail());
                                        contact.setName(contactUser.getNome());

                                        databaseReference.setValue(contact);

                                    } else {
                                        Toast.makeText(MainActivity.this, getString(R.string.user_not_found), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }

                    }
                }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        })
                .setView(editText)
                .create()
                .show();

    }

    private void signOutUser() {

        firebaseAuth.signOut();
        startActivity(new Intent(MainActivity.this, SignInActivity.class));
        finish();
    }
}
