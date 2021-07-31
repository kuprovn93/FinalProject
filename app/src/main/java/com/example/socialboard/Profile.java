package com.example.socialboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static android.content.ContentValues.TAG;

public class Profile extends AppCompatActivity {

    TextView profFirstName, profLastName, profEmail, profRole;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    Button connectAccBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profFirstName = findViewById(R.id.profile_firstName);
        profLastName = findViewById(R.id.profile_lastName);
        profEmail = findViewById(R.id.profile_email);
        profRole = findViewById(R.id.profile_role);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        connectAccBtn = findViewById(R.id.connectAccountBtn);

        if (fAuth.getCurrentUser().getEmail() == null){
            connectAccBtn.setVisibility(View.VISIBLE);
        }else{
            connectAccBtn.setVisibility(View.VISIBLE);
        }

        connectAccBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ChangeDetails.class));
                finish();
            }
        });

        DocumentReference df = fStore.collection("Users").document(fAuth.getUid());
        df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String email = (String) document.getData().get("Email");
                        String fName = (String) document.getData().get("FirstName");
                        String lName = (String) document.getData().get("LastName");
                        String role = (String) document.getData().get("UserRole");
                        profFirstName.setText(fName);
                        profLastName.setText(lName);
                        profEmail.setText(email);
                        profRole.setText(role);
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
}