package catan.player;

import java.util.List;

import catan.board.PublicBoard;
import catan.events.Action;


public abstract class CatanPlayer {
    public abstract int[] getStartingPosition(PublicBoard board);
    public abstract Action chooseAction(PublicBoard board, List<Action> possibleActions);
    public abstract int[] discardHalfOfHand(PublicBoard board, int[] hand, int numToDiscard);
    public abstract Action respondToTrade(PublicBoard board, Action trade);
}

