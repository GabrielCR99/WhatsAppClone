package com.cursoandroid.whatsappclone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cursoandroid.whatsappclone.R;
import com.cursoandroid.whatsappclone.helper.Preferences;
import com.cursoandroid.whatsappclone.model.Message;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends ArrayAdapter<Message> {

    private Context context;
    private ArrayList<Message> messages;


    public MessageAdapter(Context c, ArrayList<Message> objects) {
        super(c, 0, objects);
        this.context = c;
        this.messages = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;

        //verifica se a lista esta preenchida
        if (messages != null) {

            //recupera dados do usuario remetente
            Preferences preferences = new Preferences(context);
            String userSenderId = preferences.getIdentifier();

            //inicializa objeto para montagem do layout
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            //recupera mensagem
            Message mensagem = messages.get(position);
            //monta view a partir do xml
            if (userSenderId.equals(mensagem.getUserId())) {
                view = layoutInflater.inflate(R.layout.right_message_item, parent, false);
            }else {
                view = layoutInflater.inflate(R.layout.left_message_item, parent, false);

            }

            //recupera elemento para exibicao
            TextView message = view.findViewById(R.id.tv_message);
            message.setText(mensagem.getUserMessage());
        }

        return view;
    }
}
