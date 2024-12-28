
package catan.player;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import catan.board.CatanBoard;
import catan.events.Action;
import catan.utils.AdjacentDicts;

public class SmartRandomPlayer extends CatanPlayer {
    private final Random rand;
    
    public SmartRandomPlayer() { 
        this.rand = new Random();
    }

    @Override
    public int[] getStartingPosition(CatanBoard board) {
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
        int vertex = validVertices[rand.nextInt(validVerticesCount)];
        int edge = AdjacentDicts.vertexAdjacentEdges[vertex][rand.nextInt(3)];
        if(edge == -1) {
            edge = AdjacentDicts.vertexAdjacentEdges[vertex][rand.nextInt(2)];
        }
        return new int[] {vertex, edge};
    }

    private Action chooseStartingSettlement(CatanBoard board, List<Action> possibleActions) {
        
        while(possibleActions.size() > 1) {
            Collections.shuffle(possibleActions, rand);
            Action action = possibleActions.get(0);
            if(AdjacentDicts.vertexAdjacentTiles[action.getArgs()[0]][2] != -1) {
                return action;
            }
            possibleActions.remove(0);
        }
        return possibleActions.get(0);
    }

    @Override
    public Action chooseAction(CatanBoard board, List<Action> possibleActions) {
        if(possibleActions.get(possibleActions.size() - 1).getType() == Action.ActionType.BUILD_SETTLEMENT) {
            return chooseStartingSettlement(board, possibleActions);
        }
        return possibleActions.get(rand.nextInt(possibleActions.size()));
    }
}