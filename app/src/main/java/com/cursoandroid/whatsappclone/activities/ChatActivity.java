package com.cursoandroid.whatsappclone.activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.cursoandroid.whatsappclone.R;
import com.cursoandroid.whatsappclone.adapter.MessageAdapter;
import com.cursoandroid.whatsappclone.config.ConfiguracaoFirebase;
import com.cursoandroid.whatsappclone.helper.Base64Custom;
import com.cursoandroid.whatsappclone.helper.Preferences;
import com.cursoandroid.whatsappclone.model.Chat;
import com.cursoandroid.whatsappclone.model.Message;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {

    private EditText editText;
    private ImageButton imageButton;
    private DatabaseReference reference;
    private ListView listView;
    private ArrayList<Message> messages;
    private ArrayAdapter<Message> adapter;
    private ValueEventListener valueEventListener;

    //dados do destinatario
    private String userNameDestination;
    private String userDestinationId;

    //dados do remetente
    private String userIdSender;
    private String userSenderName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = findViewById(R.id.tb_chat);
        editText = findViewById(R.id.edit_message);
        imageButton = findViewById(R.id.send_message);
        listView = findViewById(R.id.chat_lv);

        //dados do usuario logado
        Preferences preferences = new Preferences(ChatActivity.this);
        userIdSender = preferences.getIdentifier();
        userSenderName = preferences.getNome();

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            userNameDestination = bundle.getString("name");
            String destinationEmail = bundle.getString("email");
            userDestinationId = Base64Custom.base64Code(Objects.requireNonNull(destinationEmail));
        }

        toolbar.setTitle(userNameDestination);
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        setSupportActionBar(toolbar);

        //montar listView e adapter
        messages = new ArrayList<>();
        adapter = new MessageAdapter(ChatActivity.this, messages);

        listView.setAdapter(adapter);


        //recupera mensagens do firebase
        reference = ConfiguracaoFirebase.getFirebase()
                .child("Message")
                .child(userIdSender)
                .child(userDestinationId);

        //listener para mensagens
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messages.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Message message = data.getValue(Message.class);
                    messages.add(message);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        reference.addValueEventListener(valueEventListener);


        //send message
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = editText.getText().toString();

                if (messageText.isEmpty()) {
                    Toast.makeText(ChatActivity.this, "Type a message", Toast.LENGTH_LONG).show();
                } else {

                    Message message = new Message();
                    message.setUserId(userIdSender);
                    message.setUserMessage(messageText);


                    //save message to sender
                    boolean senderMessageReturn = saveMessage(userIdSender, userDestinationId, message);
                    if (!senderMessageReturn) {
                        Toast.makeText(ChatActivity.this, "Erro!", Toast.LENGTH_SHORT).show();
                    } else {
                        //save message to destination
                        boolean destinationMessageReturn = saveMessage(userDestinationId, userIdSender, message);
                        if (!destinationMessageReturn) {
                            Toast.makeText(ChatActivity.this, "Erro!", Toast.LENGTH_SHORT).show();
                        }
                    }


                    //save chat to sender
                    Chat chat = new Chat();
                    chat.setUserId(userDestinationId);
                    chat.setName(userNameDestination);
                    chat.setMessage(messageText);
                    boolean senderChatReturn = saveChat(userIdSender, userDestinationId, chat);
                    if (!senderChatReturn) {
                        Toast.makeText(ChatActivity.this, "Erro ao salvar conversa!", Toast.LENGTH_SHORT).show();
                    } else {

                        //save chat to destination
                        chat = new Chat();
                        chat.setUserId(userIdSender);
                        chat.setName(userSenderName);
                        chat.setMessage(messageText);

                        boolean destinationChatReturn = saveChat(userDestinationId, userIdSender, chat);
                        if (!destinationChatReturn) {
                            Toast.makeText(ChatActivity.this, "Erro ao salvar conversa!", Toast.LENGTH_SHORT).show();
                        }

                        saveChat(userDestinationId, userIdSender, chat);

                    }


                    //save chat to destination
                    editText.setText("");

                }
            }
        });
    }

    private boolean saveMessage(String senderId, String destinationId, Message message) {
        try {
            reference = ConfiguracaoFirebase.getFirebase().child("Message");
            reference.child(senderId)
                    .child(destinationId)
                    .push()
                    .setValue(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean saveChat(String senderId, String destinationId, Chat chat) {

        try {

            reference = ConfiguracaoFirebase.getFirebase().child("Chat");
            reference.child(senderId)
                    .child(destinationId)
                    .setValue(chat);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }



    @Override
    protected void onStop() {
        super.onStop();
        reference.removeEventListener(valueEventListener);
    }
}
