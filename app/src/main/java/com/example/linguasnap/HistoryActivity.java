package com.example.linguasnap;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
    ListView listhistory;
    ArrayList<User> userList = new ArrayList<>();
    ArrayAdapter<User> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        email_user=findViewById(R.id.email_user);
        email_user.setText(mUser.getEmail());
        String email= email_user.getText().toString().trim();
        int index = email.indexOf(".");
        String result = email.substring(0, index);
        DatabaseReference usersRef = database.getReference().child("User");
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    for (DataSnapshot userchild : data.child(result).getChildren()) {
                       String inputText = userchild.child("inputtext").getValue(String.class);
                        String translateText = userchild.child("translatetext").getValue(String.class);
                        User user = new User(inputText, translateText);
                        userList.add(user);
                }
            }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("TAG", "Error: " + error.getMessage());
            }
        });
        ArrayAdapter<User> adapter = new ArrayAdapter<User>(this, android.R.layout.simple_list_item_1, userList);
        ListView listhistory = findViewById(R.id.listhistory);
        listhistory.setAdapter(adapter);
    }
}