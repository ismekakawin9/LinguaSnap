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

class BookMarkAdapter extends BaseAdapter {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser mUser = mAuth.getCurrentUser();
    String key = mUser.getUid();

    ArrayList<User> bookmarkList;
    public  BookMarkAdapter(ArrayList<User> bookmarkList){
        super();
        this.bookmarkList= bookmarkList;
    }
    @Override
    public int getCount() {
        return bookmarkList.size();
    }
    @Override
    public Object getItem(int position) {
        return bookmarkList.get(position);
    }
    @Override
    public long getItemId(int i) {
        return 0;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View viewUser;
        if(convertView == null) {
            viewUser = View.inflate(parent.getContext(), R.layout.bookmarkitem, null);
        }
        else
            viewUser = convertView;
        User user = bookmarkList.get(position);
        TextView txtfrom, txtto, txtinput, txttranslate;
        txtfrom = viewUser.findViewById(R.id.txtfrom);
        txtfrom.setText(user.getFrom());
        txtto = viewUser.findViewById(R.id.txtto);
        txtto.setText(user.getTo());
        txtinput = viewUser.findViewById(R.id.txtinput);
        txtinput.setText(user.getInputtext());
        txttranslate = viewUser.findViewById(R.id.txttranslate);
        txttranslate.setText(user.getTranslatetext());
        ImageView btnlike = viewUser.findViewById(R.id.btnlike);
        btnlike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().child("User").child(key).child("Bookmark").child(user.getKeyid()).removeValue();
                User user = bookmarkList.remove(position);
                notifyDataSetChanged();
            }
        });
        return viewUser;
    }
}
