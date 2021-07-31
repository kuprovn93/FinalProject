package model;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialboard.Board;
import com.example.socialboard.ChatActivity;
import com.example.socialboard.MainActivity;
import com.example.socialboard.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class BoardAdapter extends RecyclerView.Adapter<BoardAdapter.ViewHolder> {
    Context boardActivity;
    ArrayList<Boards> boardArrayList;
    public BoardAdapter(Board boardActivity, ArrayList<Boards> boardArrayList) {
        this.boardActivity = boardActivity;
        this.boardArrayList = boardArrayList;

    }

    @NotNull
    @Override
    public BoardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_users_row, parent, false);
        return new BoardAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull BoardAdapter.ViewHolder holder, int position) {
        Boards boards = boardArrayList.get(position);
        System.out.println("Board"+ boards);
        holder.boardName.setText(boards.boardName);
        holder.boardLinearLayout.setBackgroundColor(holder.itemView.getResources().getColor(getRandomColor(), null));

        // onclick on the user chat start\
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Inside on click view");
                Intent chatIntent = new Intent(boardActivity, ChatActivity.class);
                chatIntent.putExtra("boardName", boards.getBoardName());
                chatIntent.putExtra("boardUid", boards.getBoardUid());
                boardActivity.startActivity(chatIntent);
            }
        });
        //  onclick on the user chat end
    }

    private int getRandomColor() {
        List<Integer> colorCode = new ArrayList<>();
        colorCode.add(R.color.green);
        colorCode.add(R.color.yellow);
        colorCode.add(R.color.lime);
        colorCode.add(R.color.olive);

        colorCode.add(R.color.aqua);
        colorCode.add(R.color.gray);
        colorCode.add(R.color.teal);

        colorCode.add(R.color.red);
        colorCode.add(R.color.fuchsia);

        Random randomColor = new Random();
        int number = randomColor.nextInt(colorCode.size());
        return colorCode.get(number);


    }


    @Override
    public int getItemCount() {
        return boardArrayList.size();
    }

    class ViewHolder extends  RecyclerView.ViewHolder {

        TextView boardName;
        LinearLayout boardLinearLayout;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            boardName = itemView.findViewById(R.id.board_name);
            boardLinearLayout = itemView.findViewById(R.id.boardLinesrLayout);


        }
    }
}
