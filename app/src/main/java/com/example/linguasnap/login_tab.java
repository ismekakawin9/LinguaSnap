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

import com.example.linguasnap.main.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login_tab extends Fragment {

    private static  EditText email,pass;
    private static  Button btnlogin, forgot;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.login_tab, container, false);
        mAuth= FirebaseAuth.getInstance();
        email = (EditText) root.findViewById(R.id.email);
        pass = (EditText) root.findViewById(R.id.pass);
        btnlogin = (Button) root.findViewById(R.id.btnlogin);
        forgot = (Button) root.findViewById(R.id.forgot);
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentforgot = new Intent(getActivity(), FogotPassWordActivity.class);
                startActivity(intentforgot);
            }
        });

        btnlogin.setOnClickListener(new View.OnClickListener() {
            Activity activity = getActivity();
            @Override
            public void onClick(View view) {
                String account, password;
                account=email.getText().toString().trim();
                password=pass.getText().toString().trim();
                if(TextUtils.isEmpty(account)&&TextUtils.isEmpty(password)){
                    Toast.makeText(activity,"Please enter your username and password",Toast.LENGTH_LONG).show();
                }
                if(TextUtils.isEmpty(account)){
                    Toast.makeText(activity, "Please enter your username",Toast.LENGTH_LONG).show();
                    return;
                }
                if(account.length()<10){
                    Toast.makeText(activity, "Email too short, enter minimum 10 characters!",Toast.LENGTH_LONG).show();
                    return;
                }
                if(account.length()>50){
                    Toast.makeText(activity, "Email too long, enter maximum 50 characters!",Toast.LENGTH_LONG).show();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(activity, "Please enter your password",Toast.LENGTH_LONG).show();
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
                mAuth.signInWithEmailAndPassword(account,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getActivity(),  "Login Succesfully",Toast.LENGTH_LONG).show();
                            Intent intentmain = new Intent(getActivity(), MainActivity.class);
                            intentmain.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            intentmain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intentmain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intentmain);
                        }
                        else if (!task.isSuccessful()){
                            Toast.makeText(getActivity(),"username or password is incorrect. Please check and try again",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
        return root;
    }
}

