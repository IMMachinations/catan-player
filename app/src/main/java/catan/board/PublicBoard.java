package catan.board;

import catan.enums.EdgeState;

public class PublicBoard {
    private int playerId;
    private CatanBoard board;

    public PublicBoard(int playerId, CatanBoard board) {
        this.playerId = playerId;
        this.board = board;
    }

    public EdgeState getEdge(int edge) {
        if(edge < 0 || edge >= 72) {
            throw new IllegalArgumentException("Edge must be between 0 and 71");
        }
        return board.edges[edge];
    }
}