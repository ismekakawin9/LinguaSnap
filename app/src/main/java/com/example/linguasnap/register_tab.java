package com.example.linguasnap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class register_tab extends Fragment {
    private static EditText email,pass, passcof;
    private static Button signup;
    private static FirebaseAuth mAuth;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.register_tab, container, false);
        mAuth= FirebaseAuth.getInstance();
        email = (EditText) root.findViewById(R.id.email);
        pass = (EditText) root.findViewById(R.id.pass);
        passcof = (EditText) root.findViewById(R.id.passconf) ;
        signup = (Button) root.findViewById(R.id.signup);

        signup.setOnClickListener(new View.OnClickListener() {
            Activity activity = getActivity();
            @Override
            public void onClick(View view) {
                String account, password, confirm;
                account=email.getText().toString().trim();
                password=pass.getText().toString().trim();
                confirm=passcof.getText().toString().trim();
                if(TextUtils.isEmpty(account)){
                    Toast.makeText(activity, "Please enter your email address",Toast.LENGTH_LONG).show();
                    return;
                }
                if(account.length()<10){
                    Toast.makeText(activity, "Email too short, enter minimum 10 characters!",Toast.LENGTH_LONG).show();
                    return;
                }
                if(account.length()>40){
                    Toast.makeText(activity, "Email too long, enter maximum 50 characters!",Toast.LENGTH_LONG).show();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(activity, "Enter your password",Toast.LENGTH_LONG).show();
                    return;
                }
                if(password.length()<8){
                    Toast.makeText(activity, "Password too short, enter minimum 8 characters!",Toast.LENGTH_LONG).show();
                    return;
                }
                if(password.length()>30){
                    Toast.makeText(activity, "Password too long, enter maximum 30 characters!",Toast.LENGTH_LONG).show();
                    return;
                }
                if(password.equals(confirm)==false) {
                    Toast.makeText(activity, "The entered passwords do not match. Try again.", Toast.LENGTH_LONG).show();
                }
                else{
                    mAuth.createUserWithEmailAndPassword(account,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getActivity(), "You have successfully registered",Toast.LENGTH_LONG).show();
                                Intent intentlogin = new Intent(getActivity(),activity_loginds.class);
                                startActivity(intentlogin);
                            }

                            else
                                Toast.makeText(getActivity(),"User name is already taken, or the Email address is badly formatted. Try another again",Toast.LENGTH_LONG).show();
                        }
                    });
                }

            }
        });
        return root;
    }
}
