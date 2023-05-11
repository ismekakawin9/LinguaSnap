package com.example.linguasnap;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class changepass extends AppCompatActivity {
    EditText updatepass;
    Button btnupass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepass);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        updatepass = findViewById(R.id.updatepass);
        btnupass = findViewById(R.id.btnupass);
        btnupass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newpass = updatepass.getText().toString().trim();
                if(TextUtils.isEmpty(newpass)){
                    Toast.makeText(getApplication(),"Please enter your username and password",Toast.LENGTH_LONG).show();
                    return;
                }
                user.updatePassword(newpass)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(changepass.this,"User password updated", Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(changepass.this,"Fail to update your password",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }
}