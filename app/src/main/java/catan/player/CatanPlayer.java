package catan.player;

import catan.board.CatanBoard;

public abstract class CatanPlayer {
    public abstract int[] getStartingPosition(CatanBoard board);
}

