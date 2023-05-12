package com.example.linguasnap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.linguasnap.main.MainActivity;
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
    UserAdapter userAdapter;
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
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    userList.add(user);
                    userAdapter = new UserAdapter(userList);
                    listhistory.setAdapter(userAdapter);
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

            }

            private void ShowItemhistory(int position) {
                User user = userList.get(position);
                Intent intent = new Intent(HistoryActivity.this, MainActivity.class);
                intent.putExtra("getinputfromhistory", user.getInputtext());
                intent.putExtra("getoutputfromhistory", user.getTranslatetext());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }
    public void onBackPressed(){
        finish();
    }

}