
package catan.player;

import java.util.Random;

import catan.board.CatanBoard;
import catan.utils.VertexAdjacent;

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
        int vertex = validVertices[rand.nextInt(validVerticesCount)];
        int edge = VertexAdjacent.vertexAdjacentEdges[vertex][rand.nextInt(3)];
        if(edge == -1) {
            edge = VertexAdjacent.vertexAdjacentEdges[vertex][rand.nextInt(2)];
        }
        return new int[] {vertex, edge};
    }
}