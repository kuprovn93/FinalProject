package model;

public class Boards {
    String boardUid;
    String boardName;
    String boardImage;

    public Boards() {
    }

    public Boards(String boardImg, String boardName, String boardUid) {
        this.boardUid = boardUid;
        this.boardName = boardName;
        this.boardImage = boardImg;
    }

    public String getBoardUid() {
        return boardUid;
    }

    public void setBoardUid(String boardUid) {
        this.boardUid = boardUid;
    }

    public String getBoardName() {
        return boardName;
    }

    public void setBoardName(String boardName) {
        this.boardName = boardName;
    }

    public String getBoardImage() {
        return boardImage;
    }

    public void setBoardImage(String boardImg) {
        this.boardImage = boardImg;
    }
}
