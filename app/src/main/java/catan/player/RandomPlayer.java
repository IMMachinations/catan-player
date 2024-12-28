
package catan.player;

import java.util.List;
import java.util.Random;

import catan.board.CatanBoard;
import catan.events.Action;
import catan.utils.AdjacentDicts;

public class RandomPlayer extends CatanPlayer {
    private final Random rand;
    
    public RandomPlayer() { 
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
    public Action chooseAction(CatanBoard board, List<Action> possibleActions) {
        return possibleActions.get(rand.nextInt(possibleActions.size()));
    }
}