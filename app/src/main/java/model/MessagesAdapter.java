package model;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.socialboard.ChatActivity;
import com.example.socialboard.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;


public class MessagesAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<Messages> messagesArrayList;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    int ITEM_SEND_TEXT = 1;
    int ITEM_RECEIVE_TEXT = 2;
    int ITEM_SEND_IMAGE = 3;
    int ITEM_RECEIVE_IMAGE = 4;

    public MessagesAdapter(Context context, ArrayList<Messages> messagesArrayList) {
        this.context = context;
        this.messagesArrayList = messagesArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        if (viewType==1){
            View view = LayoutInflater.from(context).inflate(R.layout.sender_layout_item, parent, false);
            return new SenderViewHolder(view);
        }
        else if (viewType==2){
            View view = LayoutInflater.from(context).inflate(R.layout.receiver_layout, parent, false);
            return new ReceiverViewHolder(view);
        }
        else if (viewType==3){
            View view = LayoutInflater.from(context).inflate(R.layout.sender_layout_item_image, parent, false);
            return new SenderViewHolder(view);
        }
        else if (viewType==4){
            View view = LayoutInflater.from(context).inflate(R.layout.receiver_layout_image, parent, false);
            return new ReceiverViewHolder(view);
        }
        else{
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull  RecyclerView.ViewHolder holder, int position) {
        Messages messages = messagesArrayList.get(position);

        if (holder.getClass()==SenderViewHolder.class){
            SenderViewHolder viewHolder = (SenderViewHolder) holder;
            if (messages.getType().equals("gif")){
                viewHolder.textMessage.setVisibility(View.INVISIBLE);
                Glide.with(this.context)
                        .load(messages.getMessage())
                        .into(viewHolder.textMessageImage);

            }
            else if (messages.getType().equals("image")) {
                viewHolder.textMessage.setVisibility(View.INVISIBLE);
                Picasso.get().load(messages.getMessage()).into(viewHolder.textMessageImage);
            }
            else if (messages.getType().equals("pdf")) {
                viewHolder.textMessage.setVisibility(View.INVISIBLE);
                viewHolder.textMessageImage.setBackgroundResource(R.drawable.file);

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(messages.getMessage()));
                        viewHolder.itemView.getContext().startActivity(intent);
                    }
                });
            }
            else {
                viewHolder.textMessage.setText(messages.getMessage());
            }
            viewHolder.userName.setText("You");
            viewHolder.msgTime.setText(DateFormat.format("MM/dd/yyyy", new Date(messages.getTimestamp())).toString());


        }else{
            ReceiverViewHolder viewHolder = (ReceiverViewHolder) holder;
            if (messages.getType().equals("gif")){
                viewHolder.textMessage.setVisibility(View.INVISIBLE);
                Glide.with(this.context)
                        .load(messages.getMessage())
                        .into(viewHolder.textMessageImage);

            }
            else if (messages.getType().equals("image")) {
                viewHolder.textMessage.setVisibility(View.INVISIBLE);
                Picasso.get().load(messages.getMessage()).into(viewHolder.textMessageImage);
            }
            else if (messages.getType().equals("pdf")) {
                viewHolder.textMessage.setVisibility(View.INVISIBLE);
                viewHolder.textMessageImage.setBackgroundResource(R.drawable.file);

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(messages.getMessage()));
                        viewHolder.itemView.getContext().startActivity(intent);
                    }
                });
            }
            else{
                viewHolder.textMessage.setText(messages.getMessage());
            }
            viewHolder.userName.setText(messages.getSenderUserName());
            viewHolder.msgTime.setText(DateFormat.format("MM/dd/yyyy", new Date(messages.getTimestamp())).toString());

        }
    }

    @Override
    public int getItemCount() {
        return messagesArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Messages message = messagesArrayList.get(position);
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(message.getSenderID())){
            if (message.getType().equals("image") || message.getType().equals("pdf")){
                return ITEM_SEND_IMAGE;
            }
            return ITEM_SEND_TEXT;
        }else{
            if (message.getType().equals("image") || message.getType().equals("pdf")){
                return ITEM_RECEIVE_IMAGE;
            }
            return ITEM_RECEIVE_TEXT;
        }
    }

    class SenderViewHolder extends RecyclerView.ViewHolder{
        TextView textMessage, userName, msgTime;
        ImageView textMessageImage;


        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.txtMessages);
            userName = itemView.findViewById(R.id.messageUsername);
            msgTime = itemView.findViewById(R.id.messageTime);
            textMessageImage = itemView.findViewById(R.id.txtMessagesImage);
        }
    }
    class ReceiverViewHolder extends RecyclerView.ViewHolder{
        TextView textMessage, userName, msgTime;
        ImageView textMessageImage;


        public ReceiverViewHolder(@NonNull  View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.txtMessages);
            userName = itemView.findViewById(R.id.messageUsername);
            msgTime = itemView.findViewById(R.id.messageTime);
            textMessageImage = itemView.findViewById(R.id.txtMessagesImage);
        }
    }
}
