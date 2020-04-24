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

public class Register extends AppCompatActivity {
EditText password,repPass,email;
Button regBtn;
    FirebaseAuth fAuthorize;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
try{
 this.getSupportActionBar().hide();
}
catch (NullPointerException e){}

        password=findViewById(R.id.regPasswordET);
        email=findViewById(R.id.regMailET);
        repPass=findViewById(R.id.regRepeatPassword);
        regBtn=findViewById(R.id.regButton);
         fAuthorize = FirebaseAuth.getInstance();
         if(fAuthorize.getCurrentUser()!=null){
             startActivity(new Intent(getApplicationContext(),MainActivity.class));
             finish();
         }
        regBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
            String emailTxt=email.getText().toString().trim();
            String passwordTxt=password.getText().toString().trim();
            String reppasswordTxt=repPass.getText().toString().trim();
                Log.i("login",emailTxt);
                Log.i("login",passwordTxt);
            if(TextUtils.isEmpty(emailTxt)){
                Toast.makeText(Register.this, "Uzupełnij email", Toast.LENGTH_SHORT).show();

                email.setError("Pole wymagane");
                email.requestFocus();
                Toast.makeText(Register.this, "Uzupełnij email", Toast.LENGTH_SHORT).show();

                return;
            }
                if(TextUtils.isEmpty(passwordTxt)){
                    password.setError("Pole wymagane");
                    Toast.makeText(Register.this, "Uzupełnij hasło", Toast.LENGTH_LONG).show();
                    password.requestFocus();

                    return;

                }

                if(passwordTxt.length()<6) {
                    password.setError("Hasło musi mieć wiecej niż 6 znaków");
                    password.requestFocus();

                    return;


                }
                if(!passwordTxt.equals(reppasswordTxt)){
                    password.setError("Hasła muszą być takie same");
                    repPass.setError("Hasła muszą być takie same");
                    Toast.makeText(Register.this, "Hasła muszą być takie same ", Toast.LENGTH_LONG).show();
                    password.requestFocus();
                    repPass.requestFocus();

                    return;

                }
                fAuthorize.createUserWithEmailAndPassword(emailTxt,passwordTxt).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Register.this, "Udało się", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {
                            String errorMessage = task.getException().getMessage();
                            Toast.makeText(Register.this, "Error : " + errorMessage, Toast.LENGTH_LONG).show();

                        }
                    }
                    });

            }
        });

    }

   public void goToLogin(View view){
       startActivity(new Intent(getApplicationContext(), Login.class));

   }

}
