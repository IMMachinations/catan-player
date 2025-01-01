package catan.board;

import catan.enums.EdgeState;

public class PublicBoard {
    private int playerId;
    private CatanBoard board;

    public PublicBoard(CatanBoard board, int playerId) {
        this.playerId = playerId;
        this.board = board;
    }

    public EdgeState getEdge(int edge) {
        if(edge < 0 || edge > 71) {
            throw new IllegalArgumentException("Edge must be between 0 and 71 inclusive");
        }
        return board.getEdge(edge);
    }

    public int[] getHand() {
        return board.getPlayerHand(this.playerId);
    }

    public int getPlayerId() {
        return this.playerId;
    }

    public boolean isValidSettlementPlacement(int vertex) {
        return board.isValidSettlementPlacement(vertex);
    }
}