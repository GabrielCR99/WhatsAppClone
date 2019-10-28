package com.cursoandroid.whatsappclone.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.cursoandroid.whatsappclone.R;
import com.cursoandroid.whatsappclone.activities.ChatActivity;
import com.cursoandroid.whatsappclone.adapter.ContactAdapter;
import com.cursoandroid.whatsappclone.config.ConfiguracaoFirebase;
import com.cursoandroid.whatsappclone.helper.Preferences;
import com.cursoandroid.whatsappclone.model.Contact;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment {

    private ListView listView;
    private ArrayAdapter<Contact> arrayAdapter;
    private ArrayList<Contact> contacts;
    private DatabaseReference reference;
    private ValueEventListener valueEventListenerContacts;


    public ContactsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        reference.addValueEventListener(valueEventListenerContacts);
    }

    @Override
    public void onStop() {
        super.onStop();
        reference.removeEventListener(valueEventListenerContacts);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //instanciar objetos
        contacts = new ArrayList<>();

        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        listView = view.findViewById(R.id.lv_contacts);
        /*
        arrayAdapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()),
                android.R.layout.simple_list_item_1,
                contacts);*/
        arrayAdapter = new ContactAdapter(getActivity(), contacts);


        listView.setAdapter(arrayAdapter);

        //recupera contatos
        reference = ConfiguracaoFirebase.getFirebase()
                .child("Contatos")
                .child(new Preferences(Objects.requireNonNull(getActivity())).getIdentifier());

        //listener para recuperar contatos
        valueEventListenerContacts = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //limpar lista
                contacts.clear();

                //listar contatos
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Contact contact = snapshot.getValue(Contact.class);
                    contacts.add(contact);

                }

                arrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getActivity(), ChatActivity.class);


                //recupera dados a serem passados
                Contact contact = contacts.get(position);
                //enviando dados para chat activity
                intent.putExtra("name", contact.getName());
                intent.putExtra("email", contact.getEmail());
                startActivity(intent);

            }
        });


        return view;
    }

}
