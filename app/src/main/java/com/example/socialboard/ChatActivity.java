package com.example.socialboard;

import android.content.ClipDescription;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.os.BuildCompat;
import androidx.core.view.inputmethod.EditorInfoCompat;
import androidx.core.view.inputmethod.InputConnectionCompat;
import androidx.core.view.inputmethod.InputContentInfoCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import model.Boards;
import model.Messages;
import model.MessagesAdapter;

import static android.content.ContentValues.TAG;

public class ChatActivity extends AppCompatActivity {

    String board_name, board_uid, sender_uid;
    CircleImageView profileImage;
    TextView boardName;
    FirebaseDatabase fDatabase;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String senderRoom, receiverRoom;

    CardView sendBtn, sendDocBtn;
    EditText editMessage;

    RecyclerView messageAdapter;
    ArrayList<Messages> messageList;
    MessagesAdapter chatAdapter;
    private String checker = "", myUrl = null;
    private Uri fileUri;
    private StorageTask uploadTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.toolbar);


        board_name = getIntent().getStringExtra("boardName");
        board_uid = getIntent().getStringExtra("boardUid");
        setTitle(board_name);

        profileImage = findViewById(R.id.profile_image);
        sendBtn = findViewById(R.id.sendBtn);
        sendDocBtn = findViewById(R.id.sendDocBtn);
        editMessage = findViewById(R.id.messageToSend);
        fDatabase = FirebaseDatabase.getInstance();
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();


        boardName = findViewById(R.id.chatBoard_name);
        boardName.setText(board_name);

        sender_uid = fAuth.getUid();


        messageAdapter = findViewById(R.id.messageAdapter);
        messageList= new ArrayList<>();
        LinearLayoutManager LLM = new LinearLayoutManager(this);
        chatAdapter = new MessagesAdapter(ChatActivity.this, messageList);
        LLM.setStackFromEnd(true);
        messageAdapter.setLayoutManager(LLM);
        messageAdapter.setAdapter(chatAdapter);

        DatabaseReference chatReference = fDatabase.getReference().child("Chats").child(board_uid).child("Messages");

        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                for(DataSnapshot dsnapshot: snapshot.getChildren()){
                    Messages messages = dsnapshot.getValue(Messages.class);
                    messageList.add(messages);
                }
                chatAdapter.notifyDataSetChanged();
                messageAdapter.smoothScrollToPosition(messageAdapter.getAdapter().getItemCount());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


//        fStore.collection("Boards").document(board_uid).collection("Messages")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            messageList.clear();
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                messageList.add(document.toObject(Messages.class));
////                                Boards board = (Boards) document.getData();
//                                System.out.println(messageList);
//
////                                boardAdapter.holder.boardName.setText(board.getBoardName());
//                            }
//                        } else {
//                            Log.d(TAG, "Error getting documents: ", task.getException());
//                        }
//                        chatAdapter.notifyDataSetChanged();
//                    }
//                });


//        DatabaseReference chatReference = fDatabase.getReference().child("Chats").child(senderRoom).child("Messages");
//        chatReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                messageList.clear();
//                for(DataSnapshot dsnapshot: snapshot.getChildren()){
//                    Messages messages = dsnapshot.getValue(Messages.class);
//                    messageList.add(messages);
//                }
//                chatAdapter.notifyDataSetChanged();
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = editMessage.getText().toString();
                if (message.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Message is empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                editMessage.setText("");
                Date date = new Date();
                String senderUserName = fAuth.getCurrentUser().getEmail().split("@")[0];

                Messages messages = new Messages(message, fAuth.getUid(), senderUserName , date.getTime(), "text");
                System.out.println("senderUserName"+senderUserName);
                fDatabase.getReference().child("Chats")
                        .child(board_uid)
                        .child("Messages")
                        .push()
                        .setValue(messages);

            }
        });

//        sendBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String message = editMessage.getText().toString();
//                if (message.isEmpty()){
//                    Toast.makeText(getApplicationContext(), "Message is empty", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                editMessage.setText("");
//                Date date = new Date();
//                // Messages messages = new Messages(message, fAuth.getUid(), date.getTime());
//                Map<String, Object> data = new HashMap<>();
//                data.put("message", message);
//                data.put("senderID", fAuth.getUid());
//                data.put("timestamp", date);
//
//
//
//                fStore.collection("Boards").document(board_uid).collection("Messages").add(data)
//                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                            @Override
//                            public void onSuccess(DocumentReference documentReference) {
//                                Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
//                            }
//                        })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Log.w(TAG, "Error adding document", e);
//                            }
//                        });
//
//            }
//        });

        sendDocBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence opions[] = new CharSequence[]{
                        "Images",
                        "GIFs",
                        "Documents"
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                builder.setTitle("Select the Image");
                builder.setItems(opions, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                if (i == 0){
                                    checker = "image";
                                    Intent intent = new Intent();
                                    intent.setAction(Intent.ACTION_GET_CONTENT);
                                    intent.setType("image/*");
                                    startActivityForResult(Intent.createChooser(intent, "Select Image"), 400);

                                }
                                if (i == 1){
                                    checker = "gif";
                                    Intent intent = new Intent();
                                    intent.setAction(Intent.ACTION_GET_CONTENT);
                                    intent.setType("image/gif");
                                    startActivityForResult(Intent.createChooser(intent, "Select GIF"), 400);

                                }
                                if (i == 2){
                                    checker = "pdf";
                                    Intent intent = new Intent();
                                    intent.setAction(Intent.ACTION_GET_CONTENT);
                                    intent.setType("application/pdf");
                                    startActivityForResult(Intent.createChooser(intent, "Select PDF"), 400);



                                }

                            }
                        });
                //Creating dialog box
                AlertDialog alert = builder.create();
                //Setting the title manually

                alert.show();
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 400 && resultCode==RESULT_OK && data != null && data.getData() != null){
            fileUri = data.getData();
            if (checker.equals("pdf")){
                Toast.makeText(this, "PDF Selected", Toast.LENGTH_SHORT).show();
                StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("PDF Files");
                DatabaseReference dbRef = fDatabase.getReference().child("Chats")
                        .child(board_uid)
                        .child("Messages")
                        .push();
                String imgMsgId = dbRef.getKey();
                StorageReference filepath = storageRef.child(imgMsgId+"."+"pdf");

                uploadTask = filepath.putFile(fileUri);
                uploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull @NotNull Task task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return filepath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Uri> task) {
                        if (task.isSuccessful()){
                            Uri downloadUrl = task.getResult();
                            myUrl = downloadUrl.toString();

                            Date date = new Date();
                            String senderUserName = fAuth.getCurrentUser().getEmail().split("@")[0];

                            Messages messages = new Messages(myUrl, fAuth.getUid(), senderUserName , date.getTime(), "pdf");
                            System.out.println("senderUserName"+senderUserName);
                            fDatabase.getReference().child("Chats")
                                    .child(board_uid)
                                    .child("Messages")
                                    .push()
                                    .setValue(messages);
                            Toast.makeText(getApplicationContext(), "PDF Sent", Toast.LENGTH_SHORT).show();

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(getApplicationContext(), "File Not Sent! Try Again", Toast.LENGTH_SHORT).show();

                    }
                });
            }
            else if (checker.equals("gif")){
                Toast.makeText(this, "GIF Selected", Toast.LENGTH_SHORT).show();
                StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("GIF Files");
                DatabaseReference dbRef = fDatabase.getReference().child("Chats")
                        .child(board_uid)
                        .child("Messages")
                        .push();
                String imgMsgId = dbRef.getKey();
                StorageReference filepath = storageRef.child(imgMsgId+"."+"gif");

                uploadTask = filepath.putFile(fileUri);
                uploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull @NotNull Task task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return filepath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Uri> task) {
                        if (task.isSuccessful()){
                            Uri downloadUrl = task.getResult();
                            myUrl = downloadUrl.toString();

                            Date date = new Date();
                            String senderUserName = fAuth.getCurrentUser().getEmail().split("@")[0];

                            Messages messages = new Messages(myUrl, fAuth.getUid(), senderUserName , date.getTime(), "gif");
                            System.out.println("senderUserName"+senderUserName);
                            fDatabase.getReference().child("Chats")
                                    .child(board_uid)
                                    .child("Messages")
                                    .push()
                                    .setValue(messages);
                            Toast.makeText(getApplicationContext(), "GIF Sent", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
            else if (checker.equals("image")){
                StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("Image Files");
                DatabaseReference dbRef = fDatabase.getReference().child("Chats")
                        .child(board_uid)
                        .child("Messages")
                        .push();
                String imgMsgId = dbRef.getKey();
                StorageReference filepath = storageRef.child(imgMsgId+"."+"jpg");

                uploadTask = filepath.putFile(fileUri);
                uploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull @NotNull Task task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return filepath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Uri> task) {
                        if (task.isSuccessful()){
                            Uri downloadUrl = task.getResult();
                            myUrl = downloadUrl.toString();

                            Date date = new Date();
                            String senderUserName = fAuth.getCurrentUser().getEmail().split("@")[0];

                            Messages messages = new Messages(myUrl, fAuth.getUid(), senderUserName , date.getTime(), "image");
                            System.out.println("senderUserName"+senderUserName);
                            fDatabase.getReference().child("Chats")
                                    .child(board_uid)
                                    .child("Messages")
                                    .push()
                                    .setValue(messages);
                            Toast.makeText(getApplicationContext(), "Image Sent", Toast.LENGTH_SHORT).show();

                        }
                    }
                });

            }
            else{
                Toast.makeText(this, "Nothing Selected", Toast.LENGTH_SHORT).show();
            }


        }
    }




}