package com.cursoandroid.whatsappclone.model;

import com.cursoandroid.whatsappclone.config.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

public class User {

    private String id, nome, email, password;

    public User() {

    }

    public void save() {
        DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebase();
        databaseReference.child("Users")
                .child(getId())
                //passando o valor do proprio objeto 'usuario'
                .setValue(this);
    }

    @Exclude
    private String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
