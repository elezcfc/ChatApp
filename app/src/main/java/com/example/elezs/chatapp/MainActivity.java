package com.example.elezs.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.elezs.chatapp.domen.Message;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {

    private static final int SIGN_IN_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        handleUserAuthentication();

        FloatingActionButton submitButton = (FloatingActionButton) findViewById(R.id.submitBtn);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText input = (EditText) findViewById(R.id.inputEditText);
                FirebaseDatabase.getInstance().getReference().push().setValue(new Message(input.getText().toString(), getUserName()));

                input.setText("");
            }
        });

    }


    private void handleUserAuthentication() {

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_REQUEST_CODE);
        } else {
            Toast.makeText(this, "Welcome " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), Toast.LENGTH_LONG).show();
            displayChatMessages();
        }
    }

    private String getUserName() {
        return FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
    }

    private void displayChatMessages() {
        ListView messageListView = (ListView) findViewById(R.id.list_of_messages);
        messageListView.setDivider(getResources().getDrawable(R.drawable.divider));
        FirebaseListAdapter<Message> adapter = setupAdapter();
        messageListView.setAdapter(adapter);
    }

    private FirebaseListAdapter setupAdapter() {
        return new FirebaseListAdapter<Message>(this, Message.class, R.layout.list_item_message, FirebaseDatabase.getInstance().getReference()) {
            @Override
            protected void populateView(View v, Message model, int position) {
                TextView user = (TextView) v.findViewById(R.id.user_text_view);
                TextView message = (TextView) v.findViewById(R.id.message_text_view);
                TextView time = (TextView) v.findViewById(R.id.time_text_view);
                LinearLayout linearLayout = (LinearLayout) v.findViewById(R.id.message_holder_layout);

                TextView user_away = (TextView) v.findViewById(R.id.user_text_view_away);
                TextView message_away = (TextView) v.findViewById(R.id.message_text_view_away);
                TextView time_away = (TextView) v.findViewById(R.id.time_text_view_away);
                LinearLayout linearLayout_away = (LinearLayout) v.findViewById(R.id.message_holder_layout_away);

                user.setText(model.getUser()+":");
                user_away.setText(model.getUser()+":");
                boolean sameUser = model.getUser().equals(getUserName());
                if (sameUser) {
                    linearLayout.setEnabled(true);
                    linearLayout_away.setEnabled(true);
                    linearLayout_away.setVisibility(View.GONE);
                } else {
                    linearLayout.setEnabled(false);
                    linearLayout_away.setEnabled(false);
                    linearLayout.setVisibility(View.GONE);
                }
                message.setText(model.getText());
                message_away.setText(model.getText());

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy (HH:mm)");
                time.setText(simpleDateFormat.format(model.getTime()));
                time_away.setText(simpleDateFormat.format(model.getTime()));
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Successfully signed in. Welcome!", Toast.LENGTH_LONG).show();
                displayChatMessages();
            } else {
                Toast.makeText(this, "We couldn't sign you in. Please try again later.", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_sign_out) {
            AuthUI.getInstance().signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(MainActivity.this, "You have been signed out.", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });
        }
        return true;
    }
}
