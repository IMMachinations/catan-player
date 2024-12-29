package catan.board;



public class PublicBoard {
    private int playerId;
    private CatanBoard board;

    public PublicBoard(int playerId, CatanBoard board) {
        this.playerId = playerId;
        this.board = board;
    }
}