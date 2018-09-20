package com.example.elezs.chatapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.example.elezs.chatapp.R;
import com.example.elezs.chatapp.domen.Message;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.List;

public class ListAdapter extends ArrayAdapter<Message> {
    private List<Message> messages;
    private Context context;

    public ListAdapter(@NonNull Context context, int resource, List<Message> messages) {
        super(context, resource);
        this.messages = messages;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            v = LayoutInflater.from(context).inflate(R.layout.list_item_message, parent);
        }

        Message m = messages.get(position);

        TextView user = (TextView) v.findViewById(R.id.user_text_view);
        TextView message = (TextView) v.findViewById(R.id.message_text_view);
        TextView time = (TextView) v.findViewById(R.id.time_text_view);
        LinearLayout linearLayout = (LinearLayout) v.findViewById(R.id.item_root_layout);

        user.setText(m.getUser() + ":");
        boolean sameUser = m.getUser().equals(getUserName());


        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        if (sameUser) {
            linearLayout.setEnabled(true);
            layoutParams.gravity = Gravity.START;
        } else {
            linearLayout.setEnabled(false);
            layoutParams.gravity = Gravity.END;
        }
        linearLayout.setLayoutParams(layoutParams);

        message.setText(m.getText());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy (HH:mm)");
        time.setText(simpleDateFormat.format(m.getTime()));
        return v;
    }

    private String getUserName() {
        return FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
    }
}
