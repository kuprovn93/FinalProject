package com.example.socialboard;

import android.app.Notification;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import model.BoardAdapter;
import model.Boards;
import model.UserAdapter;
import model.Users;

import static android.content.ContentValues.TAG;

public class Board extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView nav_view;
    FloatingActionButton addBoardBtn;
    FirebaseAuth fAuth;
    RecyclerView boardList;
    BoardAdapter boardAdapter;
    FirebaseDatabase fDatabase;
    ArrayList<Boards> boardArrayList;
    AlertDialog.Builder builder;
    TextView nav_header_useremail, nav_header_username;
    private InterstitialAd interstitialAd;
    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712";

//    FirestoreRecyclerAdapter<Boards, BoardsViewHolder> boardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        boardList = findViewById(R.id.boardListRecyclerView);
        addBoardBtn = findViewById(R.id.addPostFloatBtn);
        fAuth = FirebaseAuth.getInstance();
        fDatabase = FirebaseDatabase.getInstance();
        boardArrayList = new ArrayList<Boards>();
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        drawerLayout = findViewById(R.id.drawer);
        nav_view = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        nav_view.setNavigationItemSelectedListener(this);
        setSupportActionBar(toolbar);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
//        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();



        if (savedInstanceState ==null){
            nav_view.setCheckedItem(R.id.settings);
        }
        DocumentReference df = fStore.collection("Users").document(fAuth.getCurrentUser().getUid());
        df.get().addOnSuccessListener(documentSnapshot -> {

            // identify user
            //Notification.Builder addPostFloatBtn;
            if (documentSnapshot.getString("UserRole").equals("Admin")){
                addBoardBtn.setVisibility(View.VISIBLE);

            }
            else{
                addBoardBtn.setVisibility(View.INVISIBLE);
            }

        });

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });

        loadAd();



        nav_header_useremail = findViewById(R.id.useremail);
        nav_header_username = findViewById(R.id.userName);

//        nav_header_useremail.setText(fAuth.getCurrentUser().getEmail());
//        nav_header_username.setText(fAuth.getCurrentUser().getEmail().split("@")[0]);
//        addBoardBtn.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), CreatePost.class)));
        addBoardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Board.this);
                builder.setTitle("Add a New Board");

// Set up the input
                final EditText input = new EditText(Board.this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT );
                builder.setView(input);

// Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String m_Text = input.getText().toString();
                        if (!m_Text.isEmpty()) {
                            CollectionReference df = fStore.collection("Boards");
                            Map<String, Object> postInfo = new HashMap<>();
                            postInfo.put("boardImage", "set url manually");
                            postInfo.put("boardName", m_Text);
                            postInfo.put("boardUid", "");
                            Toast.makeText(Board.this, "Board Created !", Toast.LENGTH_SHORT).show();
                            df.add(postInfo);
                            dialog.cancel();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });


        fStore.collection("Boards")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                boardArrayList.add(document.toObject(Boards.class));
//                                Boards board = (Boards) document.getData();
                                System.out.println(boardArrayList);

//                                boardAdapter.holder.boardName.setText(board.getBoardName());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        boardAdapter.notifyDataSetChanged();
                    }
                });








        // creating board list start

//        Query query = fStore.collection("Boards");
//        System.out.println("QUERY: "+query.get());

//        FirestoreRecyclerOptions<Boards> allBoards= new FirestoreRecyclerOptions.Builder<Boards>()
//                .setQuery(query, Boards.class)
//                .build();
//        System.out.println("allBoards"+allBoards);
//        boardAdapter = new FirestoreRecyclerAdapter<Boards, BoardsViewHolder>(allBoards) {
//            @Override
//            protected void onBindViewHolder(@NonNull BoardsViewHolder holder, int position, @NonNull Boards board) {
//                System.out.println("boardname: "+board.getBoardName());
//                holder.boardName.setText(board.getBoardName());
//                holder.itemView.setClickable(true);
//                holder.itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent chatIntent = new Intent(getApplicationContext(), ChatActivity.class);
//                        chatIntent.putExtra("boardName", board.getBoardName());
//                        chatIntent.putExtra("boardUid", board.getBoardUid());
//                        startActivity(chatIntent);
//                    }
//                });
//
//            }
//
//
//
////            @NonNull
////            @Override
////            public BoardsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
////                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_users_row, parent,false);
////                return new BoardsViewHolder(view);
////            }
//        };
        boardAdapter =new BoardAdapter(Board.this, boardArrayList);
        boardList.setLayoutManager(new LinearLayoutManager(this));
        boardList.setAdapter(boardAdapter);



       // addPostBtn.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), CreatePost.class)));


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.boards):
                showInterstitial();
//                if(getApplicationContext() != Board.class){
//
//                }
                Toast.makeText(Board.this, "Boards", Toast.LENGTH_SHORT).show();
                break;
            case (R.id.profile):
                startActivity(new Intent(getApplicationContext(), Profile.class));
                Toast.makeText(getApplicationContext(), "My Profile", Toast.LENGTH_SHORT).show();
                break;
            case (R.id.settings):
                startActivity(new Intent(getApplicationContext(), Settings.class));
                break;
            case (R.id.share):
                ApplicationInfo app = getApplicationContext().getApplicationInfo();
                String filePath = app.publicSourceDir;
                Uri uri = Uri.parse(filePath);
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("*/*");
                // Append file and send Intent
                File originalApk = new File(filePath);
                String shareBody = "SocialBoard: Developed By Kenvin";
                String shareSub = "SocialBoard";
                sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareSub);

                startActivity(Intent.createChooser(sharingIntent, "Share app using"));

                break;

            default:
            Toast.makeText(Board.this, "Coming Soon", Toast.LENGTH_SHORT).show();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

//    public static class BoardsViewHolder extends RecyclerView.ViewHolder{
//        TextView boardName;
//        View view;
//        public BoardsViewHolder(@NonNull  View itemView) {
//            super(itemView);
//            boardName = itemView.findViewById(R.id.board_name);
//            view = itemView;
//        }
//    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        boardAdapter.startListening();
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        if(boardAdapter != null){
//            boardAdapter.stopListening();
//        }
//    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

    public void loadAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(
                this,
                AD_UNIT_ID,
                adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        Board.this.interstitialAd = interstitialAd;
                        Log.i(TAG, "onAdLoaded");
                        Toast.makeText(Board.this, "onAdLoaded()", Toast.LENGTH_SHORT).show();
                        interstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        // Called when fullscreen content is dismissed.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        Board.this.interstitialAd = null;
                                        Log.d("TAG", "The ad was dismissed.");
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                                        // Called when fullscreen content failed to show.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        Board.this.interstitialAd = null;
                                        Log.d("TAG", "The ad failed to show.");
                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                        // Called when fullscreen content is shown.
                                        Log.d("TAG", "The ad was shown.");
                                    }
                                });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i(TAG, loadAdError.getMessage());
                        interstitialAd = null;

                        String error =
                                String.format(
                                        "domain: %s, code: %d, message: %s",
                                        loadAdError.getDomain(), loadAdError.getCode(), loadAdError.getMessage());
                        Toast.makeText(
                                Board.this, "onAdFailedToLoad() with error: " + error, Toast.LENGTH_SHORT)
                                .show();
                    }
                });
    }

    private void showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and restart the game.
        if (interstitialAd != null) {
            interstitialAd.show(this);
        } else {
            Toast.makeText(this, "Ad did not load", Toast.LENGTH_SHORT).show();
        }
    }
}