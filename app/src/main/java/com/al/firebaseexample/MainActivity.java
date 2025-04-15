package com.al.firebaseexample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    onSignInResult(result);
                }
            }
    );
    private final AuthManager authManager = new AuthManager();
    private final MessageManager messageManager = new MessageManager(authManager);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        this.findViewById(R.id.button_sign_in).setOnClickListener(v -> {
            Intent signInIntent = AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(authManager.getAuthUIProviders())
                    .build();
            signInLauncher.launch(signInIntent);
        });
        this.findViewById(R.id.button_sign_out).setOnClickListener(v -> {
            authManager.signOut();
            setScreen();
        });
        RecyclerView recyclerView = findViewById(R.id.recyclerview_messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MessageAdapter adapter = new MessageAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);
        EditText editText = this.findViewById(R.id.edittext_message);
        this.findViewById(R.id.button_send_message).setOnClickListener(v -> {
            if (!editText.getText().toString().isEmpty()) {
                messageManager.sendMessage(editText.getText().toString());
            }
        });

        final Observer<ArrayList<Message>> messagesObserver = new Observer<ArrayList<Message>>() {
            @Override
            public void onChanged(@Nullable final ArrayList<Message> messages) {
                adapter.setMessages(messages);
            }
        };

        messageManager.getMessages().observe(this, messagesObserver);

        setScreen();
    }

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        Toast toast;
        if (result.getResultCode() == RESULT_OK) {
            toast = Toast.makeText(this, "Success auth",Toast.LENGTH_LONG);
        } else {
            toast = Toast.makeText(this, "Failed auth",Toast.LENGTH_LONG);
        }
        toast.show();

        setScreen();
    }

    private void setScreen() {
        if (authManager.isAuth()) {
            this.findViewById(R.id.button_sign_in).setVisibility(View.GONE);
            this.findViewById(R.id.button_sign_out).setVisibility(View.VISIBLE);
            this.findViewById(R.id.text_messages_title).setVisibility(View.VISIBLE);
            this.findViewById(R.id.recyclerview_messages).setVisibility(View.VISIBLE);
            this.findViewById(R.id.edittext_message).setVisibility(View.VISIBLE);
            this.findViewById(R.id.button_send_message).setVisibility(View.VISIBLE);
        } else {
            this.findViewById(R.id.button_sign_in).setVisibility(View.VISIBLE);
            this.findViewById(R.id.button_sign_out).setVisibility(View.GONE);
            this.findViewById(R.id.text_messages_title).setVisibility(View.GONE);
            this.findViewById(R.id.recyclerview_messages).setVisibility(View.GONE);
            this.findViewById(R.id.edittext_message).setVisibility(View.GONE);
            this.findViewById(R.id.button_send_message).setVisibility(View.GONE);
        }
    }
}