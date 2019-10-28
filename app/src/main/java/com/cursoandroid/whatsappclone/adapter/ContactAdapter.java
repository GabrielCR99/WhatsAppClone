package com.cursoandroid.whatsappclone.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cursoandroid.whatsappclone.R;
import com.cursoandroid.whatsappclone.model.Contact;

import java.util.ArrayList;

public class ContactAdapter extends ArrayAdapter<Contact> {

    private ArrayList<Contact> contactArrayList;
    private Context context;



    public ContactAdapter(Context context,  ArrayList<Contact> objects) {
        super(context, 0, objects);
        this.contactArrayList = objects;
        this.context = context;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        View view = null;

        //verifica se a lista está vazia
        if (contactArrayList != null){



            //inicializa objeto para montagem da view
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            //monta view a partir do xml
            view = layoutInflater.inflate(R.layout.contact_list, parent, false);

            //recupera elemento para exibição

            TextView contactName = view.findViewById(R.id.tv_nome);
            TextView emailContact = view.findViewById(R.id.tv_email);

            Contact contact = contactArrayList.get(position);
            contactName.setText(contact.getName());
            emailContact.setText(contact.getEmail());


        }

        return view;
    }
}
