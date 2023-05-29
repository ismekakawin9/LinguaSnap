package com.example.linguasnap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.linguasnap.main.MainActivity;
import com.example.linguasnap.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    TextView email_user;
    String key;
    ListView listhistory;
    UserAdapter userAdapter;
    Button btn_clear;

    @SuppressLint("SuspiciousIndentation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        listhistory= findViewById(R.id.listhistory);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        email_user = findViewById(R.id.email_user);
        email_user.setText(mUser.getEmail());
        String key = mUser.getUid();
        DatabaseReference usersRef = database.getReference("User").child(key).child("History");
        ArrayList<User> userList;
        userList = new ArrayList<>();
        btn_clear= findViewById(R.id.btn_clearhis);
        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usersRef.removeValue();
                userAdapter.notifyDataSetChanged();
            }
        });
            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    userList.add(user);
                    userAdapter = new UserAdapter(userList);
                    listhistory.setAdapter(userAdapter);
                    userAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        listhistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long i) {
                ShowItemhistory(position);
                userAdapter.notifyDataSetChanged();
            }
            public void ShowItemhistory(int position) {
                 User user = userList.get(position);
                 Intent intent = new Intent(HistoryActivity.this, MainActivity.class);
                 intent.putExtra("likeorunlike",user.getLike());
                 intent.putExtra("getinputfromhistory", user.getInputtext());
                 intent.putExtra("getoutputfromhistory", user.getTranslatetext());
                 intent.putExtra("getfrom", user.getFrom());
                 intent.putExtra("getto", user.getTo());
                 setResult(Activity.RESULT_OK,intent);
                 finish();
            }
        });
    }

}