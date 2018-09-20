package com.example.elezs.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.Gravity.END;
import static android.view.Gravity.START;

public class MainActivity extends AppCompatActivity {

    private static final int SIGN_IN_REQUEST_CODE = 1;
    List<Message> messages = new ArrayList<>();

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
//        List<Message> messages = getListOfMessages();
//        ListAdapter listAdapter = new ListAdapter(this, R.layout.list_item_message, messages);
        FirebaseListAdapter<Message> adapter = setupAdapter();
        messageListView.setAdapter(adapter);
    }

    private List<Message> getListOfMessages() {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                Collection<Object> values = map.values();
                Message message = new Message();
                for (Object o : values) {
                    message = convertToMessage((HashMap<String, Object>) o);
                    messages.add(message);
                }
                System.out.println(message);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
        return messages;
    }

    private FirebaseListAdapter setupAdapter() {
        return new FirebaseListAdapter<Message>(this, Message.class, R.layout.list_item_message, FirebaseDatabase.getInstance().getReference()) {
            @Override
            protected void populateView(View v, Message model, int position) {

                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                        Collection<Object> values = map.values();
                        Message message = new Message();
                        for (Object o : values) {
                            message = convertToMessage((HashMap<String, Object>) o);
                            messages.add(message);
                        }
                        System.out.println(message);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("The read failed: " + databaseError.getCode());
                    }
                });

                TextView user = (TextView) v.findViewById(R.id.user_text_view);
                TextView message = (TextView) v.findViewById(R.id.message_text_view);
                TextView time = (TextView) v.findViewById(R.id.time_text_view);
                LinearLayout rootLayout = (LinearLayout) v.findViewById(R.id.item_root_layout);
                LinearLayout linearLayoutMessage = (LinearLayout) v.findViewById(R.id.message_holder_layout);

                user.setText(model.getUser() + ":");
                boolean sameUser = model.getUser().equals(getUserName());

                if (sameUser) {
                    linearLayoutMessage.setEnabled(true);
                    rootLayout.setGravity(START);
                } else {
                    linearLayoutMessage.setEnabled(false);
                    rootLayout.setGravity(END);
                }

                message.setText(model.getText());

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy (HH:mm)");
                time.setText(simpleDateFormat.format(model.getTime()));
            }
        };
    }

    private Message convertToMessage(HashMap<String, Object> o) {
        Message m = new Message();
        m.setUser((String) o.get("user"));
        m.setText((String) o.get("text"));
//        String s = o.get("time");
//        long time = Long.valueOf(s);
        m.setTime((Long) o.get("time"));
        return m;
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
