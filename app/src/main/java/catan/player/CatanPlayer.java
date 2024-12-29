package catan.player;

import java.util.List;

import catan.board.CatanBoard;
import catan.events.Action;


public abstract class CatanPlayer {
    public abstract int[] getStartingPosition(CatanBoard board);
    public abstract Action chooseAction(CatanBoard board, List<Action> possibleActions);
    public abstract int[] discardHalfOfHand(CatanBoard board, int[] hand, int numToDiscard);
}

