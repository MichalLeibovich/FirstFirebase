package com.example.firstfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth fUser = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (fUser.getCurrentUser() != null) {
            // means they've already signed the app and needs to move to next page
            String mail = fUser.getCurrentUser().getEmail();
            String userId = fUser.getCurrentUser().getUid();
            Toast.makeText(this, mail, Toast.LENGTH_LONG).show();
        }
    }

    public void register(View view) {
        EditText etEmail = findViewById(R.id.etTextEmailAddress);
        EditText etPassword = findViewById(R.id.etTextPassword);
        EditText etName = findViewById(R.id.etTextName);

        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String nickName = etName.getText().toString();

        fUser.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // can be done only register success
                            Log.d("FIREBASE", "onComplete: success");

                            // add user to firestore DB
                            // 1 create User Object
                            // 2 add the object to Users collection

                            User user = new User(email, nickName);
                            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                            firestore.collection("Users").add(user)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Log.d("FIREBASE", "database success" + documentReference.toString());
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("FIREBASE", "database fail " + e.getMessage());
                                        }
                                    });



                            Intent intent = new Intent(MainActivity.this, GameActivity.class);
                            startActivity(intent);
                        } else //register fail
                        {
                            Toast.makeText(MainActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}