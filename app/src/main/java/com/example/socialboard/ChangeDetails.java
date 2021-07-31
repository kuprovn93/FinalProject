package com.example.socialboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import model.BoardAdapter;

public class ChangeDetails extends AppCompatActivity {

    private static final String TAG = "change details";
    TextView newEmail, newPass, newName;
    Button connectSaveBtn;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseDatabase fDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_details);

        newName = findViewById(R.id.connectName);
        newEmail = findViewById(R.id.connectEmail);
        newPass = findViewById(R.id.connectPassword);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        connectSaveBtn  = findViewById(R.id.connectSaveBtn);


        connectSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fname = newName.getText().toString();
                String email = newEmail.getText().toString();
                String password = newPass.getText().toString();
                String status = "Hey there! I am using LetsChat.";

                if (fname.isEmpty()){
                    newName.setError("Name is Mandatory");
                    return;
                }

                if (email.isEmpty()){
                    newEmail.setError("Email is Mandatory");
                    return;
                }
                if (password.isEmpty()){
                    newPass.setError("Password is Mandatory");
                    return;
                }
                if (password.length() < 6 ){
                    newPass.setError("Password must be >= 6 characters");
                    return;
                }
                AuthCredential credential = EmailAuthProvider.getCredential(email, password);
                fAuth.getCurrentUser().linkWithCredential(credential)
                        .addOnCompleteListener(ChangeDetails.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "linkWithCredential:success");
                                    FirebaseUser user = task.getResult().getUser();
                                    DocumentReference df = fStore.collection("Users").document(fAuth.getUid());
                                    Map<String, Object> userInfo = new HashMap<>();
                                    userInfo.put("FirstName", fname);
                                    userInfo.put("LastName", fname);
                                    userInfo.put("Email", email);
                                    df.update(userInfo);
                                    Toast.makeText(ChangeDetails.this, "Account Updation Success.",
                                            Toast.LENGTH_SHORT).show();
                                    FirebaseAuth.getInstance().signOut();

                                    startActivity(new Intent(getApplicationContext(), Login.class));
                                    finish();
                                    Toast.makeText(getApplicationContext(),"Login in using new details",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.w(TAG, "linkWithCredential:failure", task.getException());
                                    Toast.makeText(ChangeDetails.this, "Updation failed.",
                                            Toast.LENGTH_SHORT).show();

                                }
                            }
                        });            }
        });

    }
}