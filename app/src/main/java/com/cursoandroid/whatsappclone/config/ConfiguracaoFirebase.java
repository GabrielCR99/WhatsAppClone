package com.cursoandroid.whatsappclone.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public final class ConfiguracaoFirebase {

    private static DatabaseReference databaseReference;
    private static FirebaseAuth firebaseAuth;

    public static DatabaseReference getFirebase() {

        if (databaseReference == null) {
            databaseReference = FirebaseDatabase.getInstance().getReference();

        }

        return databaseReference;
    }

    public static FirebaseAuth getFirebaseAuth(){

        if (firebaseAuth == null){
            firebaseAuth = FirebaseAuth.getInstance();
        }

        return firebaseAuth;
    }
}
