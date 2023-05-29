package com.example.linguasnap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

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

public class BookmarkActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    TextView email_userbookmark;
    String key;
    ListView listbookmark;
    BookMarkAdapter bookMarkAdapter;

    @SuppressLint("SuspiciousIndentation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        listbookmark= findViewById(R.id.listbookmark);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        email_userbookmark = findViewById(R.id.email_userbookmark);
        email_userbookmark.setText(mUser.getEmail());
        String key = mUser.getUid();
        DatabaseReference usersRef = database.getReference("User").child(key).child("Bookmark");
        ArrayList<User> bookmarkList;
        bookmarkList = new ArrayList<>();
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    bookmarkList.add(user);
                    bookMarkAdapter = new BookMarkAdapter(bookmarkList);
                    listbookmark.setAdapter(bookMarkAdapter);
                    bookMarkAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        listbookmark.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long i) {
                ShowItemhistory(position);
                bookMarkAdapter.notifyDataSetChanged();
            }
            public void ShowItemhistory(int position) {
                User user = bookmarkList.get(position);
                Intent intent = new Intent(BookmarkActivity.this, MainActivity.class);
                intent.putExtra("getinputfrombookmark", user.getInputtext());
                intent.putExtra("getoutputfrombookmark", user.getTranslatetext());
                intent.putExtra("getfrom", user.getFrom());
                intent.putExtra("getto", user.getTo());
                setResult(Activity.RESULT_OK,intent);
                finish();
            }
        });
    }

}