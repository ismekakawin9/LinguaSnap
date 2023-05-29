package com.example.linguasnap;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.media.Image;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.*;
import com.example.linguasnap.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class UserAdapter extends BaseAdapter {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser mUser = mAuth.getCurrentUser();
    String key = mUser.getUid();
    private AccessibilityService adaptersContext;

    ArrayList<User> userList;
    public  UserAdapter(ArrayList<User> userList){
        super();
        this.userList= userList;
    }
    @Override
    public int getCount() {
        return userList.size();
    }
    @Override
    public Object getItem(int position) {
        return userList.get(position);
    }
    @Override
    public long getItemId(int i) {
        return 0;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View viewUser;
        if(convertView == null) {
            viewUser = View.inflate(parent.getContext(), R.layout.itemuser, null);
        }
        else
            viewUser = convertView;
        User user = userList.get(position);
        TextView txt_from, txt_to, txt_input, txt_translate;
        txt_from = viewUser.findViewById(R.id.txt_from);
        txt_from.setText(user.getFrom());
        txt_to = viewUser.findViewById(R.id.txt_to);
        txt_to.setText(user.getTo());
        txt_input = viewUser.findViewById(R.id.txt_input);
        txt_input.setText(user.getInputtext());
        txt_translate = viewUser.findViewById(R.id.txt_translate);
        txt_translate.setText(user.getTranslatetext());
        ImageView btnlike = viewUser.findViewById(R.id.btn_like);
        ImageView btnremove = viewUser.findViewById(R.id.btn_remove);
        if (user.getLike() == 0)
            btnlike.setImageResource(R.drawable.unlike);
        else
            btnlike.setImageResource(R.drawable.like);
        btnlike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.getLike() == 0) {
                    user.setLike(1);
                    btnlike.setImageResource(R.drawable.like);
                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put(user.getKeyid(), user);
                    FirebaseDatabase.getInstance().getReference().child("User").child(key).child("History").updateChildren(childUpdates);
                    DatabaseReference usersRef = database.getReference("User").child(key).child("Bookmark").child(user.getKeyid());
                    usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            usersRef.setValue(user);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                } else {
                    user.setLike(0);
                    btnlike.setImageResource(R.drawable.unlike);
                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put(user.getKeyid(), user);
                    FirebaseDatabase.getInstance().getReference().child("User").child(key).child("History").updateChildren(childUpdates);
                    FirebaseDatabase.getInstance().getReference().child("User").child(key).child("Bookmark").child(user.getKeyid()).removeValue();
                }
                notifyDataSetChanged();
            }
        });
        btnremove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().child("User").child(key).child("History").child(user.getKeyid()).removeValue();
                User user = userList.remove(position);
                notifyDataSetChanged();
            }
        });
        return viewUser;
    }
}
