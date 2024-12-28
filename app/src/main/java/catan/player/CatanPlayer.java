package catan.player;

import java.util.Random;
interface CatanPlayer {
    int[] getStartingPosition(CatanBoard board);
}

class RandomPlayer implements CatanPlayer {
    public int[] getStartingPosition(CatanBoard board) {
        Random rand = new Random();
        int[] validVertices = new int[54];
        int validVerticesCount = 0;
        for(int i = 0; i < 54; i++) {
            if(board.isValidSettlementPlacement(i)) {
                validVertices[validVerticesCount] = i;
                validVerticesCount++;
            }
        }
        int vertex = validVertices[rand.nextInt(validVerticesCount)];
        int edge = VertexAdjacent.vertexAdjacentEdges[vertex][rand.nextInt(3)];
        if(edge == -1) {
            edge = VertexAdjacent.vertexAdjacentEdges[vertex][rand.nextInt(2)];
        }
        return new int[] {vertex, edge};
    }
    
}