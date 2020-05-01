package com.example.placearchiverapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    EditText loginET,passwordET;
    Button loginBtn,regBtn;
    FirebaseAuth fba;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        try{
            this.getSupportActionBar().hide();
        }

        catch (NullPointerException e){}
        fba=FirebaseAuth.getInstance();
        if(fba.getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext(),MapsActivity.class));
            finish();
        }
        passwordET=findViewById(R.id.regLoginET);
        loginET=findViewById(R.id.loginMailET);
        loginBtn=findViewById(R.id.loginbutton);
        regBtn=findViewById(R.id.regButton);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                String emailTxt=loginET.getText().toString().trim();
                String passwordTxt=passwordET.getText().toString().trim();

                if(TextUtils.isEmpty(emailTxt)){
                    loginET.setError("Pole wymagane");
                    loginET.requestFocus();
                    return;

                }
                if(TextUtils.isEmpty(passwordTxt)){
                    passwordET.setError("Pole wymagane");
                    passwordET.requestFocus();

                    return;

                }

                fba.signInWithEmailAndPassword(emailTxt,passwordTxt).addOnCompleteListener(Login.this,new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Login.this,"Pomyslnie zalogowano",Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(),MapsActivity.class));
                        }
                        else{
                            String errorMessage = task.getException().getMessage();

                            Toast.makeText(Login.this, "Error : " + errorMessage, Toast.LENGTH_LONG).show();

                        }

                    }
                });
            }
                catch (Exception ex){
                    Toast.makeText(Login.this,"Błąd logowania",Toast.LENGTH_LONG);
                    Log.i("exception",ex.getMessage());
                }
        }});
    }

    public void register(View view){
        startActivity(new Intent(getApplicationContext(),Register.class));

    }

}
