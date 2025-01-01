
package catan.player;

import java.util.List;
import java.util.Random;
import java.util.Collections;
import java.util.Arrays;


import catan.board.PublicBoard;
import catan.events.Action;
import catan.utils.AdjacentDicts;

public class RandomPlayer extends CatanPlayer {
    private final Random rand;
    
    public RandomPlayer() { 
        this.rand = new Random();
    }

    @Override
    public int[] getStartingPosition(PublicBoard board) {
        int[] validVertices = new int[54];
        int validVerticesCount = 0;
        for(int i = 0; i < 54; i++) {
            if(board.isValidSettlementPlacement(i)) {
                validVertices[validVerticesCount] = i;
                validVerticesCount++;
            }
        }
        if(validVerticesCount == 0) {
            throw new IllegalArgumentException("No valid vertices found");
        }
        int[] smartVertices = new int[validVerticesCount];
        int smartVerticesCount = 0;
        for(int i = 0; i < validVerticesCount; i++) {
            if(AdjacentDicts.vertexAdjacentTiles[validVertices[i]][2] != -1) {
                smartVertices[smartVerticesCount] = validVertices[i];
                smartVerticesCount++;
            }
        }
        int vertex;
        if(smartVerticesCount != 0) {
            vertex = smartVertices[rand.nextInt(smartVerticesCount)];
        } else {
            for(int i = 0; i < validVerticesCount; i++) {
            if(AdjacentDicts.vertexAdjacentTiles[validVertices[i]][1] != -1) {
                smartVertices[smartVerticesCount] = validVertices[i];
                smartVerticesCount++;
                }
            }
            if(smartVerticesCount != 0) {
                vertex = smartVertices[rand.nextInt(smartVerticesCount)];
            } else {
                vertex = validVertices[rand.nextInt(validVerticesCount)];
            }
        } 
        
        int edge = AdjacentDicts.vertexAdjacentEdges[vertex][rand.nextInt(3)];
        if(edge == -1) {
            edge = AdjacentDicts.vertexAdjacentEdges[vertex][rand.nextInt(2)];
        }
        return new int[] {vertex, edge};
    }

    @Override
    public Action chooseAction(PublicBoard board, List<Action> possibleActions) {
        return possibleActions.get(rand.nextInt(possibleActions.size()));
    }

    @Override
    public int[] discardHalfOfHand(PublicBoard board, int[] hand, int numToDiscard) {
        int[] discarded = new int[5];
        int[] order = new int[] {0,1,2,3,4};
        Collections.shuffle(Arrays.asList(order));
        int count = 0;
        while(numToDiscard > 0) {
            for(int i = 0; i < 5; i++) {
                count = rand.nextInt(Math.min(numToDiscard,hand[order[i]]));
                discarded[order[i]] += count;
                numToDiscard -= count;
                hand[order[i]] -= count;
            }
        }

        return discarded;
    }

    @Override
    public Action respondToTrade(PublicBoard board, Action trade) {
        int[] getHand = board.getHand();
        int[] proposed_trade = trade.getArgs();
        boolean canAccept = true;
        for(int i = 0; i < 5; i++) {
            if(getHand[i] < proposed_trade[i]) {
                canAccept = false;
            }
        }
        if(canAccept) {
            int[] response = new int[] {proposed_trade[0], proposed_trade[1], proposed_trade[2], proposed_trade[3], proposed_trade[4], board.getPlayerId()};
            if(rand.nextBoolean()) {
                return new Action(Action.ActionType.RESPOND_TO_PLAYER_TRADE, response);
            }
        }
        return new Action(Action.ActionType.REJECT_PLAYER_TRADE, new int[] {});
    }
}