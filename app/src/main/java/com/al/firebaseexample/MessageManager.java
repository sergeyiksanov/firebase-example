package com.al.firebaseexample;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MessageManager {
    private MutableLiveData<ArrayList<Message>> messages;
    private final AuthManager authManager;
    private final DatabaseReference dbReference;

    public MessageManager(AuthManager authManager) {
        this.authManager = authManager;
        this.dbReference = FirebaseDatabase.getInstance("https://fir-example-6d157-default-rtdb.europe-west1.firebasedatabase.app").getReference();

        dbReference.child("messages").get().addOnSuccessListener(v -> {
            ArrayList<Message> messagesPreload = new ArrayList<>();
            for (DataSnapshot snapshot : v.getChildren()) {
                Message m = snapshot.getValue(Message.class);
                if (m != null) {
                    messagesPreload.add(m);
                }
            }

            messages.postValue(messagesPreload);
        });

        dbReference.child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message message = snapshot.getValue(Message.class);;
                if (message != null) {
                    if (messages == null) {
                        messages = new MutableLiveData<>();
                    }

                    ArrayList<Message> new_messages;
                    if (messages.getValue() == null) {
                        new_messages = new ArrayList<>();
                    } else {
                        new_messages = messages.getValue();
                    }
                    new_messages.add(message);
                    messages.postValue(new_messages);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {}
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    public LiveData<ArrayList<Message>> getMessages() {
        if (messages == null) {
            messages = new MutableLiveData<>();
        }

        return messages;
    }

    public void sendMessage(String content) {
        Log.d("SEND CALL", content);
        String email = authManager.getEmail();
        if (authManager.isAuth() && email != null) {
            Log.d("SEND CALL", content + '1');
            String key = dbReference.child("messages").push().getKey();
            Message message = new Message(content, email);
            if (key != null) {
                Log.d("SEND CALL", content + '2');
                dbReference.child("messages").child(key).setValue(message);
            }
        }
    }
}